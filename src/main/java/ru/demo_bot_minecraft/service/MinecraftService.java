package ru.demo_bot_minecraft.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.demo_bot_minecraft.connector.MinecraftConnector;
import ru.demo_bot_minecraft.domain.dto.ServerStats;

@Service
@RequiredArgsConstructor
public class MinecraftService {

    private final MinecraftConnector minecraftConnector;

    public ServerStats getMinecraftServerStats(String address, Integer port) {
        return minecraftConnector.sendRequest(address, port);
    }
}
