package ru.demo_bot_minecraft.replies.subscribes.downtime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.database.Subscription;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;
import ru.demo_bot_minecraft.domain.database.TelegramUser;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;

import static ru.demo_bot_minecraft.util.ReplyUtils.messageEquals;

@Component
@RequiredArgsConstructor
public class DowntimeSubscriptionReply implements Reply<Message> {

    private final SubscriptionRepository subscriptionRepository;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return messageEquals(message, RequestMessagesEnum.DOWNTIME_SUBSCRIPTION);
    }

    public BotApiMethod<?> getReply(Message message) {
        subscriptionRepository.save(Subscription.builder()
            .telegramUser(TelegramUser.builder().id(message.getFrom().getId()).build())
            .type(SubscriptionType.DOWNTIME)
            .build());
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), BotMessageEnum.DOWNTIME_SUBSCRIBED.getMessage());
        sendMessage.setReplyMarkup(keyboards.getSubscriptionsKeyboard(message.getFrom().getId()));
        return sendMessage;
    }

    @Override
    public UserState getRequiredUserState() {
        return UserState.SUBSCRIPTION;
    }

    @Override
    public boolean availableInAnyState() {
        return false;
    }
}
