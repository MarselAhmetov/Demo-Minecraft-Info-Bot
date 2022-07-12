package ru.demo_bot_minecraft.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionsAction implements Action {

    private final SubscriptionRepository subscriptionRepository;
    private final Keyboards keyboards;

    @Override
    public boolean getPredicate(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            if (message.hasText()) {
                var text = message.getText();
                if (text.equals("Subscriptions")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public BotApiMethod makeAction(Update update) {
        var subscriptions = subscriptionRepository
            .findAllByTelegramUserId(update.getMessage().getFrom().getId());

        SendMessage message;
        StringBuilder messageBuilder = new StringBuilder();
        if (!subscriptions.isEmpty()) {
            messageBuilder.append("You subscribe on: \n");
            subscriptions.forEach( subscription -> messageBuilder.append(subscription.getType().name()).append("\n"));
        } else {
            messageBuilder.append("Press buttons to subscribe on server events");
        }
        message = new SendMessage(update.getMessage().getChatId().toString(), messageBuilder.toString());
        message.setReplyMarkup(keyboards.getSubscriptionsKeyboard(subscriptions));
        return message;
    }
}
