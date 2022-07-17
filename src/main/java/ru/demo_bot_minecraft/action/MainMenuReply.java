package ru.demo_bot_minecraft.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

@Component
@RequiredArgsConstructor
public class MainMenuReply implements Reply<Message> {

    private final Keyboards keyboards;
    private final TelegramUserRepository userRepository;

    @Override
    public boolean predicate(Message message) {
        return message.getText().equalsIgnoreCase(RequestMessagesEnum.MAIN_MENU.getMessage());
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        userRepository.setState(message.getFrom().getId(), BotState.DEFAULT);
        return SendMessage.builder()
            .chatId(message.getChatId().toString())
            .text("Main menu")
            .replyMarkup(keyboards.getDefaultKeyboard())
            .build();
    }

    @Override
    public BotState getState() {
        return BotState.SUBSCRIPTION;
    }

    @Override
    public boolean availableInAnyState() {
        return true;
    }
}
