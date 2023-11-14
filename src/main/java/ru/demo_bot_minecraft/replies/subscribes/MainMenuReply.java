package ru.demo_bot_minecraft.replies.subscribes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.enums.BotMessage;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

import static ru.demo_bot_minecraft.util.ReplyUtils.messageEquals;

@Component
@RequiredArgsConstructor
public class MainMenuReply implements Reply<Message> {

    private final Keyboards keyboards;
    private final TelegramUserRepository userRepository;

    @Override
    public boolean predicate(Message message) {
        return messageEquals(message, RequestMessagesEnum.MAIN_MENU);
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        var user = userRepository.getById(message.getFrom().getId());
        userRepository.setState(message.getFrom().getId(), UserState.DEFAULT);
        return SendMessage.builder()
            .chatId(message.getChatId().toString())
            .text(BotMessage.MAIN_MENU.getMessage())
            .replyMarkup(keyboards.getDefaultKeyboard(user.getRole()))
            .build();
    }

    @Override
    public UserState getRequiredUserState() {
        return UserState.SUBSCRIPTION;
    }

    @Override
    public boolean availableInAnyState() {
        return true;
    }
}
