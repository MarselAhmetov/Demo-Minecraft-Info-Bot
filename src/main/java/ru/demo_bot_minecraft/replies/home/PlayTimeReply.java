package ru.demo_bot_minecraft.replies.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

@Component
@RequiredArgsConstructor
public class PlayTimeReply implements Reply<Message> {

    private final Keyboards keyboards;

    private final TelegramUserRepository userRepository;

    @Override
    public boolean predicate(Message message) {
        return message.getText().equalsIgnoreCase(RequestMessagesEnum.PLAY_TIME.getMessage());
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        userRepository.setState(message.getFrom().getId(), BotState.PLAY_TIME);
        return SendMessage.builder()
            .chatId(message.getChatId().toString())
            .text(BotMessageEnum.PLAY_TIME.getMessage())
            .replyMarkup(keyboards.getPlayTimeKeyboard())
            .build();
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
