package ru.demo_bot_minecraft.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.demo_bot_minecraft.domain.database.Subscription;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;
import ru.demo_bot_minecraft.domain.database.TelegramUser;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionsTypesAction implements Action {

    private final SubscriptionRepository subscriptionRepository;
    private final Keyboards keyboards;

    @Override
    public boolean getPredicate(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            if (message.hasText()) {
                var text = message.getText();
                if (text.equals("New Players") || text.equals("Players join")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public BotApiMethod makeAction(Update update) {
        var text = update.getMessage().getText();
        SendMessage message;
        StringBuilder messageBuilder = new StringBuilder();
        if (text.equals("New Players")) {
            subscriptionRepository.save(Subscription.builder()
                .telegramUser(TelegramUser.builder().id(update.getMessage().getFrom().getId()).build())
                .type(SubscriptionType.NEW_PLAYERS)
                .build());
            messageBuilder.append("Now you will receive message when new player joins to server");
        }
        if (text.equals("Players join")) {
            subscriptionRepository.save(Subscription.builder()
                .telegramUser(TelegramUser.builder().id(update.getMessage().getFrom().getId()).build())
                .type(SubscriptionType.PLAYERS_JOIN)
                .build());
            messageBuilder.append("Now you will receive message when any player joins to server");
        }
        message = new SendMessage(update.getMessage().getChatId().toString(), messageBuilder.toString());
        message.setReplyMarkup(keyboards.getDefaultKeyboard());
        return message;
    }
}
