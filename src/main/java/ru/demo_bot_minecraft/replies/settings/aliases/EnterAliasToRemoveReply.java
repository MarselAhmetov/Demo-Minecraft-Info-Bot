package ru.demo_bot_minecraft.replies.settings.aliases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.enums.BotMessage;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.PlayerAliasRepository;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

import static ru.demo_bot_minecraft.util.ReplyUtils.anyText;

@Component
@RequiredArgsConstructor
public class EnterAliasToRemoveReply implements Reply<Message> {

    private final TelegramUserRepository telegramUserRepository;
    private final PlayerAliasRepository playerAliasRepository;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return anyText(message);
    }

    @Override
    public UserState getRequiredUserState() {
        return UserState.REMOVE_ALIAS;
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        var userId = message.getFrom().getId();
        var alias = message.getText();
        playerAliasRepository.deleteByUserIdAndAlias(userId, alias);
        telegramUserRepository.setState(message.getFrom().getId(), UserState.ALIASES);
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), BotMessage.ALIAS_REMOVED.getMessage());
        sendMessage.setReplyMarkup(keyboards.getAliasesKeyboard());
        return sendMessage;
    }
}
