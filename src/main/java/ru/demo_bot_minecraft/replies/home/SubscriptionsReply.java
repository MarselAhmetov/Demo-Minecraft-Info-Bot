package ru.demo_bot_minecraft.replies.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionsReply implements Reply<Message> {

    private final SubscriptionRepository subscriptionRepository;
    private final Keyboards keyboards;


    @Override
    public boolean predicate(Message message) {
        return message.getText().equals(RequestMessagesEnum.SUBSCRIPTION.getMessage());
    }

    public BotApiMethod<?> getReply(Message message) {
        var subscriptions = subscriptionRepository
            .findAllByTelegramUserId(message.getFrom().getId());
        SendMessage sendMessage;
        StringBuilder messageBuilder = new StringBuilder();
        if (!subscriptions.isEmpty()) {
            messageBuilder.append(BotMessageEnum.CURRENT_SUBSCRIPTIONS.getMessage());
            subscriptions.forEach( subscription -> messageBuilder.append(subscription.getType().name()).append("\n"));
        } else {
            messageBuilder.append(BotMessageEnum.SUBSCRIPTION.getMessage());
        }
        sendMessage = new SendMessage(message.getChatId().toString(), messageBuilder.toString());
        sendMessage.setReplyMarkup(keyboards.getSubscriptionsKeyboard(message.getFrom().getId()));
        return sendMessage;
    }

    @Override
    public BotState getState() {
        return BotState.DEFAULT;
    }

    @Override
    public boolean availableInAnyState() {
        return false;
    }
}
