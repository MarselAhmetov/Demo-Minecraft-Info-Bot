package ru.demo_bot_minecraft.job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.demo_bot_minecraft.domain.database.PlayerAlias;
import ru.demo_bot_minecraft.domain.database.ServerInfoDowntime;
import ru.demo_bot_minecraft.domain.dto.ServerAction;
import ru.demo_bot_minecraft.domain.database.Player;
import ru.demo_bot_minecraft.domain.database.ServerEvent;
import ru.demo_bot_minecraft.domain.database.ServerStats;
import ru.demo_bot_minecraft.domain.database.Subscription;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;
import ru.demo_bot_minecraft.event.SendMessageEvent;
import ru.demo_bot_minecraft.mapper.ServerStatsMapper;
import ru.demo_bot_minecraft.repository.PlayerAliasRepository;
import ru.demo_bot_minecraft.repository.PlayerRepository;
import ru.demo_bot_minecraft.repository.ServerEventRepository;
import ru.demo_bot_minecraft.repository.ServerInfoDowntimeRepository;
import ru.demo_bot_minecraft.repository.ServerStatsRepository;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;
import ru.demo_bot_minecraft.service.MinecraftService;
import ru.demo_bot_minecraft.service.ServerInfoDowntimeService;
import ru.demo_bot_minecraft.service.SubscriptionsService;
import ru.demo_bot_minecraft.util.DateUtils;

import static ru.demo_bot_minecraft.domain.enums.BotMessage.NEW_PLAYER_JOIN;
import static ru.demo_bot_minecraft.domain.enums.BotMessage.PLAYER_JOIN;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinecraftStatusJob {

    private final MinecraftService minecraftService;
    private final PlayerRepository playerRepository;
    private final ServerEventRepository serverEventRepository;
    private final ServerStatsRepository serverStatsRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ServerInfoDowntimeRepository downtimeRepository;
    private final PlayerAliasRepository playerAliasRepository;
    private final SubscriptionsService subscriptionsService;
    private final ServerInfoDowntimeService serverInfoDowntimeService;

    private final ServerStatsMapper serverStatsMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    private LocalDateTime currentCheckTime;

    private static final List<String> ERRORS_TO_IGNORE = List.of("Connect timed out");

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void updateMinecraftInfo() {
        currentCheckTime = DateUtils.now();
        var lastCheckData = serverStatsRepository.getServerStats().orElse(null);
        var currentServerStats = minecraftService.getMinecraftServerStats();
        if (currentServerStats.getServerStats().isEmpty()) {
            if (currentServerStats.getError() != null) {
                var downtime = serverInfoDowntimeService.getCurrentDowntime()
                        .orElseGet(() -> {
                            var d = serverInfoDowntimeService.createDowntime(currentServerStats.getError());
                            if (!ERRORS_TO_IGNORE.contains(currentServerStats.getError())) {
                                subscriptionsService.sendDowntimeReportMessage(d);
                            }
                            return d;
                        });

                log.info("Server downtime in seconds: {}", ChronoUnit.SECONDS.between(downtime.getDowntime(), DateUtils.now()));
            }
        }
        currentServerStats.getServerStats().ifPresent(currentServerData -> {
            setUptimeToCurrentDowntime();
            checkEvent(currentServerData, lastCheckData);
            var serverStats = serverStatsMapper.toEntity(currentServerData);
            playerRepository.saveAll(serverStats.getPlayersOnline());
            serverStatsRepository.updateData(serverStats);
        });
    }


    private Optional<ServerInfoDowntime> setUptimeToCurrentDowntime() {
        return serverInfoDowntimeService.getCurrentDowntime().map(downtime -> {
            downtime.setUptime(DateUtils.now());
            downtime = downtimeRepository.save(downtime);
            if (!ERRORS_TO_IGNORE.contains(downtime.getError())) {
                sendUptimeReport(downtime);
            }
            return downtime;
        });
    }



    private void sendUptimeReport(ServerInfoDowntime downtime) {
        var subscriptions = subscriptionRepository.findAllByType(SubscriptionType.DOWNTIME);
        String messageBuilder = "Сервер встал в " + downtime.getUptime().format(
                DateTimeFormatter.ofPattern("dd.MM HH:mm"));
        subscriptions.stream()
                .map(Subscription::getTelegramUser)
                .distinct()
                .forEach(user -> applicationEventPublisher.publishEvent(new SendMessageEvent(this,
                        messageBuilder, user.getId().toString())));
    }

    private void checkEvent(ru.demo_bot_minecraft.domain.dto.ServerStats currentServerData, ServerStats lastCheckData) {
        if (lastCheckData != null && !isDataEquals(currentServerData, lastCheckData)) {
            var currentOnline = currentServerData.getPlayersInfo().getPlayersOnline();
            var lastCheckOnline = lastCheckData.getPlayersOnline();
            if (currentOnline == null) {
                currentOnline = new ArrayList<>();
            }
            if (lastCheckOnline == null) {
                lastCheckOnline = new ArrayList<>();
            }
            checkJoinLeftEvents(currentOnline, lastCheckOnline);
        }
    }

    private void checkJoinLeftEvents(List<Player> currentOnline, List<Player> lastCheckOnline) {
        if (!currentOnline.equals(lastCheckOnline)) {
            var joined = new ArrayList<>(currentOnline);
            var left = new ArrayList<>(lastCheckOnline);
            joined.removeAll(lastCheckOnline);
            left.removeAll(currentOnline);

            // TODO save batch
            joined.forEach(player -> {
                boolean isPlayerNew = !playerRepository.existsById(player.getId());
                if (isPlayerNew) {
                    handleNewPlayerEvent(player);
                } else {
                    handleJoinPlayerEvent(player);
                }
                saveJoinEvent(player);
            });
            if (!left.isEmpty()) {
                left.forEach(this::saveLeftEvent);
            }
        }
    }

    private void saveJoinEvent(Player player) {
        serverEventRepository.save(ServerEvent.builder()
                .player(getOrSavePlayer(player))
                .action(ServerAction.JOIN)
                .time(this.currentCheckTime)
                .build());
    }

    private void saveLeftEvent(Player player) {
        serverEventRepository.save(ServerEvent.builder()
                .player(getOrSavePlayer(player))
                .action(ServerAction.LEFT)
                .time(this.currentCheckTime)
                .build());
    }

    private void handleJoinPlayerEvent(Player player) {
        var playerLeft = serverEventRepository.existsByPlayerNameAndTimeAfterAndAction(player.getName(), currentCheckTime.minusMinutes(3), ServerAction.LEFT);
        if (playerLeft) {
            return;
        }
        var subscriptions = subscriptionRepository.findAllByType(SubscriptionType.PLAYERS_JOIN);
        var aliases = playerAliasRepository.findAllByPlayerName(player.getName()).stream()
                .collect(Collectors.toMap(p -> p.getUser().getId(), PlayerAlias::getAlias));
        subscriptions.stream()
                .map(Subscription::getTelegramUser)
                .filter(user -> !player.getName().equals(user.getMinecraftName()))
                .distinct()
                .forEach(user -> applicationEventPublisher.publishEvent(
                        new SendMessageEvent(
                                this,
                                PLAYER_JOIN.getMessage().formatted(aliases.getOrDefault(user.getId(), player.getName())),
                                user.getId().toString())
                ));
    }

    private void handleNewPlayerEvent(Player player) {
        var subscriptions = subscriptionRepository.findAllByType(SubscriptionType.NEW_PLAYERS);
        String messageBuilder = NEW_PLAYER_JOIN.getMessage().formatted(player.getName());
        subscriptions.stream()
                .map(Subscription::getTelegramUser)
                .filter(user -> !player.getName().equals(user.getMinecraftName()))
                .distinct()
                .forEach(user -> applicationEventPublisher.publishEvent(new SendMessageEvent(this,
                        messageBuilder, user.getId().toString())));
    }

    private Player getOrSavePlayer(Player player) {
        if (!playerRepository.existsById(player.getId())) {
            playerRepository.save(player);
        }
        return player;
    }

    private boolean isDataEquals(ru.demo_bot_minecraft.domain.dto.ServerStats currentServerData, ServerStats lastCheckData) {
        return lastCheckData.getName().equals(currentServerData.getVersion().getName()) &&
                lastCheckData.getMaxPlayers() == currentServerData.getPlayersInfo().getMax() &&
                lastCheckData.getOnlinePlayers() == currentServerData.getPlayersInfo().getOnline() &&
                lastCheckData.getProtocol().equals(currentServerData.getVersion().getProtocol()) &&
                lastCheckData.getText().equals(currentServerData.getDescriptionText()) &&
                lastCheckData.getPlayersOnline().size() == currentServerData.getPlayersInfo().getPlayersOnline().size() &&
                new HashSet<>(lastCheckData.getPlayersOnline()).containsAll(currentServerData.getPlayersInfo().getPlayersOnline());
    }
}
