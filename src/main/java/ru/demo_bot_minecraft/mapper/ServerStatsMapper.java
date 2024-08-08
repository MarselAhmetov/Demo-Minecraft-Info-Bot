package ru.demo_bot_minecraft.mapper;

import org.springframework.stereotype.Component;
import ru.demo_bot_minecraft.domain.database.Player;
import ru.demo_bot_minecraft.domain.database.ServerStats;

@Component
public class ServerStatsMapper {
    public ServerStats toEntity(ru.demo_bot_minecraft.domain.dto.ServerStats serverStats) {
        return ServerStats.builder()
            .text(serverStats.getDescription())
            .name(serverStats.getVersion().getName())
            .protocol(serverStats.getVersion().getProtocol())
            .maxPlayers(serverStats.getPlayersInfo().getMax())
            .onlinePlayers(serverStats.getPlayersInfo().getOnline())
            .playersOnline(serverStats.getPlayersInfo().getPlayersOnline().stream().map(player ->
                Player.builder()
                    .id(player.getId())
                    .name(player.getName())
                    .build())
                .toList())
            .build();
    }
}
