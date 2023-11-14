package ru.demo_bot_minecraft.replies.settings.aliases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

import static ru.demo_bot_minecraft.util.ReplyUtils.messageEquals;

@Component
@RequiredArgsConstructor
public class AddAliasReply implements Reply<Message> {

    private final TelegramUserRepository telegramUserRepository;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return messageEquals(message, RequestMessagesEnum.ADD_ALIAS);
    }

    @Override
    public UserState getRequiredUserState() {
        return UserState.ALIASES;
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        telegramUserRepository.setState(message.getFrom().getId(), UserState.ADD_ALIAS);
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), BotMessageEnum.ENTER_PLAYER_NAME_AND_ALIAS.getMessage());
        sendMessage.setReplyMarkup(keyboards.getAliasesKeyboard());
        return sendMessage;
    }

    @Override
    public boolean availableInAnyState() {
        return false;
    }
}
