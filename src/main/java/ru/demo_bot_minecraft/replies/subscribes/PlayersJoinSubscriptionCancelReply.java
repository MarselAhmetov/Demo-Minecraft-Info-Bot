package ru.demo_bot_minecraft.replies.subscribes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class PlayersJoinSubscriptionCancelReply implements Reply<Message> {

    private final SubscriptionRepository subscriptionRepository;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return message.getText()
            .equalsIgnoreCase(RequestMessagesEnum.CANCEL_PLAYERS_JOIN_SUBSCRIPTION.getMessage());
    }

    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        subscriptionRepository.deleteByTelegramUserIdAndType(message.getFrom().getId(), SubscriptionType.PLAYERS_JOIN);
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), BotMessageEnum.PLAYERS_JOIN_SUBSCRIPTION_CANCELED.getMessage());
        sendMessage.setReplyMarkup(keyboards.getSubscriptionsKeyboard(message.getFrom().getId()));
        return sendMessage;
    }

    @Override
    public BotState getState() {
        return BotState.SUBSCRIPTION;
    }

    @Override
    public boolean availableInAnyState() {
        return false;
    }
}
