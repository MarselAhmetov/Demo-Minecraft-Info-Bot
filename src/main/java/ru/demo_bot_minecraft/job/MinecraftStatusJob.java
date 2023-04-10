package ru.demo_bot_minecraft.job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.demo_bot_minecraft.domain.database.ServerInfoDowntime;
import ru.demo_bot_minecraft.domain.dto.ServerAction;
import ru.demo_bot_minecraft.domain.database.Player;
import ru.demo_bot_minecraft.domain.database.ServerEvent;
import ru.demo_bot_minecraft.domain.database.ServerStats;
import ru.demo_bot_minecraft.domain.database.Subscription;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;
import ru.demo_bot_minecraft.event.SendMessageEvent;
import ru.demo_bot_minecraft.mapper.ServerStatsMapper;
import ru.demo_bot_minecraft.repository.PlayerRepository;
import ru.demo_bot_minecraft.repository.ServerEventRepository;
import ru.demo_bot_minecraft.repository.ServerInfoDowntimeRepository;
import ru.demo_bot_minecraft.repository.ServerStatsRepository;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;
import ru.demo_bot_minecraft.service.MinecraftService;
import ru.demo_bot_minecraft.util.DateUtils;

import static ru.demo_bot_minecraft.domain.enums.BotMessageEnum.NEW_PLAYER_JOIN;
import static ru.demo_bot_minecraft.domain.enums.BotMessageEnum.PLAYER_JOIN;

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

    private final ServerStatsMapper serverStatsMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    private LocalDateTime currentCheckTime;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void updateMinecraftInfo() {
        currentCheckTime = DateUtils.now();
        log.info("Check server info: " + currentCheckTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        var lastCheckData = serverStatsRepository.getServerStats().orElse(null);
        var currentServerStats = minecraftService.getMinecraftServerStats();
        if (currentServerStats.getServerStats().isEmpty()) {
            if (currentServerStats.getError() != null) {
                ServerInfoDowntime downtime = getCurrentDowntime()
                        .orElseGet(() -> createDowntime(currentServerStats.getError()));
                log.info("Server downtime in seconds: " + ChronoUnit.SECONDS.between(downtime.getDowntime(), DateUtils.now()));
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

    private ServerInfoDowntime createDowntime(String error) {
        var downtime = ServerInfoDowntime.builder()
                .downtime(DateUtils.now())
                .error(error)
                .build();
        downtime = downtimeRepository.save(downtime);
        sendDowntimeReport(downtime);
        return downtime;
    }

    private Optional<ServerInfoDowntime> getCurrentDowntime() {
        return downtimeRepository.findByUptimeIsNull();
    }

    private Optional<ServerInfoDowntime> setUptimeToCurrentDowntime() {
        return getCurrentDowntime().map(downtime -> {
            downtime.setUptime(DateUtils.now());
            downtime = downtimeRepository.save(downtime);
            sendUptimeReport(downtime);
            return downtime;
        });
    }

    private void sendDowntimeReport(ServerInfoDowntime downtime) {
        var subscriptions = subscriptionRepository.findAllByType(SubscriptionType.DOWNTIME);
        String messageBuilder = "Сервер упал в " + downtime.getDowntime().format(
                DateTimeFormatter.ofPattern("dd.MM HH:mm"));
        subscriptions.stream()
                .map(Subscription::getTelegramUser)
                .distinct()
                .forEach(user -> applicationEventPublisher.publishEvent(new SendMessageEvent(this,
                        messageBuilder, user.getId().toString())));
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
        var subscriptions = subscriptionRepository.findAllByType(SubscriptionType.PLAYERS_JOIN);
        String message = PLAYER_JOIN.getMessage().formatted(player.getName());
        subscriptions.stream()
                .map(Subscription::getTelegramUser)
                .filter(user -> !player.getName().equals(user.getMinecraftName()))
                .distinct()
                .forEach(user -> applicationEventPublisher.publishEvent(new SendMessageEvent(this,
                        message, user.getId().toString())));
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
                lastCheckData.getText().equals(currentServerData.getDescription().getText()) &&
                lastCheckData.getPlayersOnline().size() == currentServerData.getPlayersInfo().getPlayersOnline().size() &&
                new HashSet<>(lastCheckData.getPlayersOnline()).containsAll(currentServerData.getPlayersInfo().getPlayersOnline());
    }
}
