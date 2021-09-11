package ru.demo_bot_minecraft.job;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.demo_bot_minecraft.ServerInfoStore;
import ru.demo_bot_minecraft.domain.ServerAction;
import ru.demo_bot_minecraft.domain.ServerEvent;
import ru.demo_bot_minecraft.domain.ServerStats;
import ru.demo_bot_minecraft.domain.TimeInterval;
import ru.demo_bot_minecraft.service.MinecraftService;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinecraftStatusJob {

    private final MinecraftService minecraftService;
    private final ServerInfoStore serverInfoStore;

    @Value("${minecraft.server.address}")
    private String address;
    @Value("${minecraft.server.port}")
    private Integer port;

    private ServerStats lastCheckData;
    private LocalDateTime currentCheckTime;

    @Scheduled(fixedDelay = 10000)
    public void updateMinecraftInfo() {
        currentCheckTime = LocalDateTime.now();
        ServerStats currentServerData = minecraftService.getMinecraftServerStats(address, port);
        log.info(currentServerData.toString());
        var newEvents = checkEvent(currentServerData, this.lastCheckData);
        if (newEvents != null && !newEvents.isEmpty()) {
            newEvents.forEach(event -> {
                var playerEvents = serverInfoStore.getPlayersEvents().computeIfAbsent(event.getPlayer().getName(), k -> new Stack<>());
                if (event.getAction().equals(ServerAction.JOIN)) {
                }
                if (event.getAction().equals(ServerAction.LEFT)) {
                    if (!playerEvents.isEmpty()) {
                        var lastEvent = playerEvents.peek();
                        if (lastEvent.getAction().equals(ServerAction.JOIN)) {
                            var intervals = serverInfoStore.getPlayingInfo().computeIfAbsent(event.getPlayer().getName(), k -> new ArrayList<>());
                            intervals.add(TimeInterval.builder()
                                .start(lastEvent.getTime())
                                .finish(event.getTime())
                                .build());
                        }
                    }
                }
                playerEvents.push(event);
            });
        }
        this.lastCheckData = currentServerData;
    }

    private List<ServerEvent> checkEvent(ServerStats currentServerData, ServerStats lastCheckData) {
        List<ServerEvent> events = new ArrayList<>();
        if (lastCheckData != null && !currentServerData.equals(lastCheckData)) {
            var currentOnline = currentServerData.getPlayersInfo().getPlayersOnline();
            var lastCheckOnline = lastCheckData.getPlayersInfo().getPlayersOnline();
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
                    joined.forEach(player -> events.add(
                        ServerEvent.builder()
                            .player(player)
                            .action(ServerAction.JOIN)
                            .time(this.currentCheckTime)
                        .build()));
                }
                if (!left.isEmpty()) {
                    left.forEach(player -> events.add(
                        ServerEvent.builder()
                            .player(player)
                            .action(ServerAction.LEFT)
                            .time(this.currentCheckTime)
                            .build()));
                }
                return events;
            }
        }
        return null;
    }
}
