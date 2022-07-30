package ru.demo_bot_minecraft.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.demo_bot_minecraft.connector.MinecraftConnector;
import ru.demo_bot_minecraft.domain.dto.ServerStatsResponse;

@Service
@RequiredArgsConstructor
public class MinecraftService {

    private final MinecraftConnector minecraftConnector;

    public ServerStatsResponse getMinecraftServerStats(String address, Integer port) {
        return minecraftConnector.sendRequest(address, port);
    }
}
