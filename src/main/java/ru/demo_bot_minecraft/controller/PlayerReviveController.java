package ru.demo_bot_minecraft.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.demo_bot_minecraft.domain.request.PlayerReviveRequest;
import ru.demo_bot_minecraft.service.PlayerReviveService;

@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.open-endpoints", havingValue = "true")
public class PlayerReviveController {

    private final PlayerReviveService playerReviveService;

    @PostMapping("/api/player/revive")
    public void sendPlayerRevive(@RequestBody PlayerReviveRequest request) {
        playerReviveService.sendPlayerRevive(request);
    }
}
