package ru.demo_bot_minecraft.replies.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.enums.BotMessage;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

import static ru.demo_bot_minecraft.util.ReplyUtils.messageEquals;

@Component
@RequiredArgsConstructor
public class SubscriptionsReply implements Reply<Message> {

    private final SubscriptionRepository subscriptionRepository;
    private final TelegramUserRepository telegramUserRepository;
    private final Keyboards keyboards;


    @Override
    public boolean predicate(Message message) {
        return messageEquals(message, RequestMessagesEnum.SUBSCRIPTION);
    }

    @Override
    public BotApiMethod<?> getReply(Message message) {
        telegramUserRepository.setState(message.getFrom().getId(), UserState.SUBSCRIPTION);
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .replyMarkup(keyboards.getSubscriptionsKeyboard(message.getFrom().getId()))
                .text(buildMessage(message))
                .parseMode(ParseMode.HTML)
                .build();
    }

    private String buildMessage(Message message) {
        var subscriptions = subscriptionRepository
                .findAllByTelegramUserId(message.getFrom().getId());
        StringBuilder messageBuilder = new StringBuilder();
        if (!subscriptions.isEmpty()) {
            messageBuilder.append(BotMessage.CURRENT_SUBSCRIPTIONS.getMessage());
            subscriptions.forEach(subscription -> messageBuilder.append(subscription.getType().name()).append("\n"));
        } else {
            messageBuilder.append(BotMessage.SUBSCRIPTION.getMessage());
        }
        return messageBuilder.toString();
    }
}
