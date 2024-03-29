package ru.demo_bot_minecraft.replies.subscribes.new_players;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;
import ru.demo_bot_minecraft.domain.enums.BotMessage;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;

import static ru.demo_bot_minecraft.util.ReplyUtils.messageEquals;

@Component
@RequiredArgsConstructor
public class NewPlayersSubscriptionCancelReply implements Reply<Message> {

    private final SubscriptionRepository subscriptionRepository;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return messageEquals(message, RequestMessagesEnum.CANCEL_NEW_PLAYERS_SUBSCRIPTION);
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        subscriptionRepository.deleteByTelegramUserIdAndType(message.getFrom().getId(), SubscriptionType.NEW_PLAYERS);
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), BotMessage.NEW_PLAYERS_SUBSCRIPTION_CANCELED.getMessage());
        sendMessage.setReplyMarkup(keyboards.getSubscriptionsKeyboard(message.getFrom().getId()));
        return sendMessage;
    }

    @Override
    public UserState getRequiredUserState() {
        return UserState.SUBSCRIPTION;
    }
}
