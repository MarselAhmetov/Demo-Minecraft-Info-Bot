package ru.demo_bot_minecraft.job;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
import ru.demo_bot_minecraft.repository.ServerStatsRepository;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;
import ru.demo_bot_minecraft.service.MinecraftService;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinecraftStatusJob {

    private final MinecraftService minecraftService;
    private final PlayerRepository playerRepository;
    private final ServerEventRepository serverEventRepository;
    private final ServerStatsRepository serverStatsRepository;
    private final SubscriptionRepository subscriptionRepository;

    private final ServerStatsMapper serverStatsMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${minecraft.server.address}")
    private String address;
    @Value("${minecraft.server.port}")
    private Integer port;

    private LocalDateTime currentCheckTime;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void updateMinecraftInfo() {
        ServerStats lastCheckData = serverStatsRepository.getServerStats().orElse(null);
        currentCheckTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        var currentServerData = minecraftService.getMinecraftServerStats(address, port);
        var newEvents = checkEvent(currentServerData, lastCheckData);
        serverStatsRepository.updateData(serverStatsMapper.toEntity(currentServerData));
    }

    private List<ServerEvent> checkEvent(
        ru.demo_bot_minecraft.domain.dto.ServerStats currentServerData, ServerStats lastCheckData) {
        List<ServerEvent> events = new ArrayList<>();

        if (lastCheckData != null && !isDataEquals(currentServerData, lastCheckData)) {
            var currentOnline = currentServerData.getPlayersInfo().getPlayersOnline();
            var lastCheckOnline = lastCheckData.getPlayersOnline();
            if (currentOnline == null) {
                currentOnline = new ArrayList<>();
            }
            if (lastCheckOnline == null) {
                lastCheckOnline = new ArrayList<>();
            }

            if (!currentOnline.equals(lastCheckOnline)) {

                var joined = new ArrayList<>(currentOnline);
                var left = new ArrayList<>(lastCheckOnline);
                joined.removeAll(lastCheckOnline);
                left.removeAll(currentOnline);

                if (!joined.isEmpty()) {
                        joined.forEach(player -> {
                            boolean isPlayerNew = !playerRepository.existsById(player.getId());
                            var subscriptions = subscriptionRepository.findAll();
                            if (isPlayerNew) {
                                String messageBuilder = "НОВЫЙ игрок : "
                                    + player.getName()
                                    + " зашел на сервер";
                                subscriptions.stream()
                                    .map(Subscription::getTelegramUser)
                                    .distinct()
                                    .forEach(user -> applicationEventPublisher.publishEvent(new SendMessageEvent(this,
                                        messageBuilder, user.getId().toString())));
                            } else {
                                String messageBuilder = player.getName()
                                    + " зашел на сервер";
                                subscriptions.stream()
                                    .filter(subscription -> subscription.getType().equals(SubscriptionType.PLAYERS_JOIN))
                                    .map(Subscription::getTelegramUser)
                                    .distinct()
                                    .forEach(user -> applicationEventPublisher.publishEvent(new SendMessageEvent(this,
                                        messageBuilder, user.getId().toString())));
                            }
                            serverEventRepository.save(ServerEvent.builder()
                                .player(getOrSavePlayer(player))
                                .action(ServerAction.JOIN)
                                .time(this.currentCheckTime)
                                .build());
                        });
                }
                if (!left.isEmpty()) {
                    left.forEach(player -> serverEventRepository.save(ServerEvent.builder()
                        .player(getOrSavePlayer(player))
                        .action(ServerAction.LEFT)
                        .time(this.currentCheckTime)
                        .build()));
                }
                return events;
            }
        }
        return null;
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
