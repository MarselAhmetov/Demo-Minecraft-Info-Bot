package ru.demo_bot_minecraft.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.demo_bot_minecraft.domain.database.PlayerAlias;
import ru.demo_bot_minecraft.domain.database.Subscription;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;
import ru.demo_bot_minecraft.domain.request.PlayerReviveRequest;
import ru.demo_bot_minecraft.event.SendMessageEvent;
import ru.demo_bot_minecraft.repository.PlayerAliasRepository;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;

import java.util.Map;
import java.util.stream.Collectors;

import static ru.demo_bot_minecraft.domain.enums.BotMessageEnum.PLAYER_WAIT_FOR_REVIVE;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.open-endpoints", havingValue = "true")
public class PlayerReviveService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final SubscriptionRepository subscriptionRepository;
    private final PlayerAliasRepository playerAliasRepository;

    public void sendPlayerRevive(PlayerReviveRequest playerReviveRequest) {
        var subscriptions = subscriptionRepository.findAllByType(SubscriptionType.PLAYERS_REVIVE);
        var aliases = playerAliasRepository.findAllByPlayerName(playerReviveRequest.getName()).stream()
                .collect(Collectors.toMap(p -> p.getUser().getId(), PlayerAlias::getAlias));
        subscriptions.stream()
                .map(Subscription::getTelegramUser)
                .filter(user -> !playerReviveRequest.getName().equals(user.getMinecraftName()))
                .distinct()
                .forEach(user -> applicationEventPublisher.publishEvent(
                        new SendMessageEvent(
                                this,
                                PLAYER_WAIT_FOR_REVIVE.getMessage()
                                        .formatted(
                                                aliases.getOrDefault(user.getId(), playerReviveRequest.getName()),
                                                getMaterialsAsText(playerReviveRequest.getMaterials())),
                                user.getId().toString())
                ));
    }

    private String getMaterialsAsText(Map<String, Integer> materials) {
        StringBuilder messageBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : materials.entrySet()) {
            messageBuilder.append(entry.getKey().replace('_', ' ')).append(" - ").append(entry.getValue()).append("\n");
        }
        return messageBuilder.toString();
    }

}
