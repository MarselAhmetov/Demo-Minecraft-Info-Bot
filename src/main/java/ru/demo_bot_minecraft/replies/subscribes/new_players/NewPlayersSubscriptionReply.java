package ru.demo_bot_minecraft.replies.subscribes.new_players;

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
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class NewPlayersSubscriptionReply implements Reply<Message> {

    private final SubscriptionRepository subscriptionRepository;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return message.getText().equalsIgnoreCase(RequestMessagesEnum.NEW_PLAYERS_SUBSCRIPTION.getMessage());
    }

    public BotApiMethod<?> getReply(Message message) {
        subscriptionRepository.save(Subscription.builder()
            .telegramUser(TelegramUser.builder().id(message.getFrom().getId()).build())
            .type(SubscriptionType.NEW_PLAYERS)
            .build());
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), BotMessageEnum.NEW_PLAYERS_SUBSCRIBED.getMessage());
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
