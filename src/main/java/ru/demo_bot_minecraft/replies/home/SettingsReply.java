package ru.demo_bot_minecraft.replies.home;

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
public class SettingsReply implements Reply<Message> {

    private final Keyboards keyboards;

    private final TelegramUserRepository userRepository;

    @Override
    public boolean predicate(Message message) {
        return messageEquals(message, RequestMessagesEnum.SETTINGS);
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        var user = userRepository.getById(message.getFrom().getId());
        user.setState(UserState.SETTINGS);
        return SendMessage.builder()
            .chatId(message.getChatId().toString())
            .text(BotMessageEnum.SETTINGS.getMessage())
            .replyMarkup(keyboards.getSettingsKeyboard(user))
            .build();
    }

    @Override
    public UserState getRequiredUserState() {
        return UserState.DEFAULT;
    }

    @Override
    public boolean availableInAnyState() {
        return false;
    }
}
