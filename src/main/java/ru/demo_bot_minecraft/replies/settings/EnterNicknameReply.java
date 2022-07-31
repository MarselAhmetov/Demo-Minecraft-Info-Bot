package ru.demo_bot_minecraft.replies.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

@Component
@RequiredArgsConstructor
public class EnterNicknameReply implements Reply<Message> {

    private final TelegramUserRepository telegramUserRepository;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return message.getText() != null;
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        var user = telegramUserRepository.getById(message.getFrom().getId());
        user.setMinecraftName(message.getText());
        user.setBotState(BotState.SETTINGS);
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), BotMessageEnum.NICKNAME_ADDED.getMessage());
        sendMessage.setReplyMarkup(keyboards.getSettingsKeyboard(user));
        return sendMessage;
    }

    @Override
    public BotState getState() {
        return BotState.ADD_NICKNAME;
    }

    @Override
    public boolean availableInAnyState() {
        return false;
    }
}
