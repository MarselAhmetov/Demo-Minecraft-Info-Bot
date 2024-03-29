package ru.demo_bot_minecraft.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.demo_bot_minecraft.domain.database.*;
import ru.demo_bot_minecraft.domain.enums.BotMessage;
import ru.demo_bot_minecraft.event.SendMessageEvent;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SubscriptionsService {
    private final SubscriptionRepository subscriptionRepository;
    private final TelegramUserRepository telegramUserRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void sendUserWaitingForApproveMessage(TelegramUser user) {
        telegramUserRepository.findAllByRole(TelegramUserRole.ADMIN).forEach(admin -> {
            SendMessageEvent event = new SendMessageEvent(this,
                    BotMessage.USER_WAITING_FOR_APPROVE.getMessage().formatted(user.getUserName()),
                    admin.getId().toString());
            applicationEventPublisher.publishEvent(event);
        });
    }

    public void sendUsersWaitingForApproveMessage() {
        var users = telegramUserRepository.findAllByStatus(TelegramUserStatus.WAITING_FOR_APPROVE);
        if (users.isEmpty()) {
            return;
        }
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(BotMessage.USERS_WAITING_FOR_APPROVE);
        users.forEach(user -> messageBuilder
                .append(user.getFirstName()).append(" ")
                .append(user.getLastName()).append(" ")
                .append(user.getUserName()).append("\n"));
        telegramUserRepository.findAllByRole(TelegramUserRole.ADMIN)
                .forEach(admin ->
                        applicationEventPublisher.publishEvent(
                                new SendMessageEvent(this, messageBuilder.toString(), admin.getId().toString())
                        ));
    }

    public void sendDowntimeReportMessage(ServerInfoDowntime downtime) {
        var subscriptions = subscriptionRepository.findAllByType(SubscriptionType.DOWNTIME);
        String message = "Сервер упал в " + downtime.getDowntime().format(
                DateTimeFormatter.ofPattern("dd.MM HH:mm"));
        subscriptions.stream()
                .map(Subscription::getTelegramUser)
                .distinct()
                .forEach(user -> applicationEventPublisher.publishEvent(new SendMessageEvent(this,
                        message, user.getId().toString())));
    }
}
