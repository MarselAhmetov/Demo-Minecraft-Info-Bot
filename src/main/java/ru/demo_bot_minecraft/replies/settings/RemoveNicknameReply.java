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
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

@Component
@RequiredArgsConstructor
public class RemoveNicknameReply implements Reply<Message> {

    private final TelegramUserRepository telegramUserRepository;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return message.getText().startsWith(RequestMessagesEnum.REMOVE_NICKNAME.getMessage());
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        var user = telegramUserRepository.getById(message.getFrom().getId());
        user.setMinecraftName(null);
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), BotMessageEnum.NICKNAME_REMOVED.getMessage());
        sendMessage.setReplyMarkup(keyboards.getSettingsKeyboard(user));
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
