package ru.demo_bot_minecraft.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.demo_bot_minecraft.domain.database.PlayerAlias;
import ru.demo_bot_minecraft.domain.database.Subscription;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;
import ru.demo_bot_minecraft.domain.request.PlayerBuybackedRequest;
import ru.demo_bot_minecraft.domain.request.PlayerDeadRequest;
import ru.demo_bot_minecraft.domain.request.PlayerRevivedRequest;
import ru.demo_bot_minecraft.event.SendMessageEvent;
import ru.demo_bot_minecraft.repository.PlayerAliasRepository;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;

import java.util.Map;
import java.util.stream.Collectors;

import static ru.demo_bot_minecraft.domain.enums.BotMessage.*;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.open-endpoints", havingValue = "true")
public class PlayerReviveService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final SubscriptionRepository subscriptionRepository;
    private final PlayerAliasRepository playerAliasRepository;

    public void sendPlayerDead(PlayerDeadRequest playerDeadRequest) {
        var subscriptions = subscriptionRepository.findAllByType(SubscriptionType.PLAYERS_REVIVE);
        var aliases = playerAliasRepository.findAllByPlayerName(playerDeadRequest.getName()).stream()
                .collect(Collectors.toMap(p -> p.getUser().getId(), PlayerAlias::getAlias));
        String buybackMessage;
        if (playerDeadRequest.getBuybackCount() != null) {
            buybackMessage =  "\n Уже возродился: %s раз".formatted(playerDeadRequest.getBuybackCount());
        } else {
            buybackMessage = "";
        }
        subscriptions.stream()
                .map(Subscription::getTelegramUser)
                .distinct()
                .forEach(user -> applicationEventPublisher.publishEvent(
                        new SendMessageEvent(
                                this,
                                PLAYER_WAIT_FOR_REVIVE.getMessage()
                                        .formatted(
                                                aliases.getOrDefault(user.getId(), playerDeadRequest.getName()),
                                                getMaterialsAsText(playerDeadRequest.getMaterials())) + buybackMessage,
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

    public void sendPlayerRevive(PlayerRevivedRequest request) {
        var subscriptions = subscriptionRepository.findAllByType(SubscriptionType.PLAYERS_REVIVE);
        var aliases = playerAliasRepository.findAllByPlayerName(request.getName()).stream()
                .collect(Collectors.toMap(p -> p.getUser().getId(), PlayerAlias::getAlias));
        var message = switch (request.getType()) {
            case BUYBACK -> PLAYER_BUYBACKED.getMessage();
            case REVIVE -> PLAYER_REVIVED.getMessage();
        };
        subscriptions.stream()
                .map(Subscription::getTelegramUser)
                .distinct()
                .forEach(user -> applicationEventPublisher.publishEvent(
                        new SendMessageEvent(
                                this,
                                message.formatted(aliases.getOrDefault(user.getId(), request.getName())), user.getId().toString()
                        )
                ));
    }

    public void sendPlayerBuybacked(PlayerBuybackedRequest request) {
        var subscriptions = subscriptionRepository.findAllByType(SubscriptionType.PLAYERS_REVIVE);
        var aliases = playerAliasRepository.findAllByPlayerName(request.getName()).stream()
                .collect(Collectors.toMap(p -> p.getUser().getId(), PlayerAlias::getAlias));
        subscriptions.stream()
                .map(Subscription::getTelegramUser)
                .distinct()
                .forEach(user -> applicationEventPublisher.publishEvent(
                        new SendMessageEvent(
                                this,
                                PLAYER_BUYBACKED.getMessage()
                                        .formatted(aliases.getOrDefault(user.getId(), request.getName())),
                                user.getId().toString()
                        )
                ));
    }
}
