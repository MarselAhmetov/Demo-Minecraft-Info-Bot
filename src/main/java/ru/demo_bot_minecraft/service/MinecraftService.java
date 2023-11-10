package ru.demo_bot_minecraft.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.demo_bot_minecraft.client.MinecraftClient;
import ru.demo_bot_minecraft.domain.dto.ServerStatsResponse;

@Service
@RequiredArgsConstructor
public class MinecraftService {

    @Value("${minecraft.server.address}")
    private String address;
    @Value("${minecraft.server.port}")
    private Integer port;

    private final MinecraftClient minecraftClient;

    public ServerStatsResponse getMinecraftServerStats() {
        return minecraftClient.sendRequest(address, port);
    }
}
