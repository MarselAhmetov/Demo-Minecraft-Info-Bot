package ru.demo_bot_minecraft.replies.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

import static ru.demo_bot_minecraft.util.ReplyUtils.messageEquals;

@Component
@RequiredArgsConstructor
public class AddNicknameReply implements Reply<Message> {

    private final TelegramUserRepository telegramUserRepository;


    @Override
    public boolean predicate(Message message) {
        return messageEquals(message, RequestMessagesEnum.ADD_NICKNAME);
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        telegramUserRepository.setState(message.getFrom().getId(), BotState.ADD_NICKNAME);
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), BotMessageEnum.ENTER_YOUR_NICKNAME.getMessage());
        sendMessage.setReplyMarkup(null);
        return sendMessage;
    }

    @Override
    public BotState getState() {
        return BotState.SETTINGS;
    }

    @Override
    public boolean availableInAnyState() {
        return false;
    }
}
