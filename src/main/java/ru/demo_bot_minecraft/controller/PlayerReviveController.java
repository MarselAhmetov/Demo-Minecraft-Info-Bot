package ru.demo_bot_minecraft.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.demo_bot_minecraft.domain.request.PlayerDeadRequest;
import ru.demo_bot_minecraft.domain.request.PlayerRevivedRequest;
import ru.demo_bot_minecraft.service.PlayerReviveService;

@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.open-endpoints", havingValue = "true")
public class PlayerReviveController {

    private final PlayerReviveService playerReviveService;

    @PostMapping("/api/player/dead")
    public void sendPlayerIsDead(@RequestBody PlayerDeadRequest request) {
        playerReviveService.sendPlayerDead(request);
    }

    @PostMapping("/api/player/revived")
    public void sendPlayerRevived(@RequestBody PlayerRevivedRequest request) {
        playerReviveService.sendPlayerRevive(request);
    }
}
