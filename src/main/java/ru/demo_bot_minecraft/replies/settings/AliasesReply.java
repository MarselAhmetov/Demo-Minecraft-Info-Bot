package ru.demo_bot_minecraft.replies.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.database.PlayerAlias;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.PlayerAliasRepository;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

import java.util.List;

import static ru.demo_bot_minecraft.util.ReplyUtils.messageEquals;

@Component
@RequiredArgsConstructor
public class AliasesReply implements Reply<Message> {

    private final TelegramUserRepository telegramUserRepository;
    private final PlayerAliasRepository playerAliasRepository;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return messageEquals(message, RequestMessagesEnum.ALIASES);
    }

    @Override
    public UserState getRequiredUserState() {
        return UserState.SETTINGS;
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        var userId = message.getFrom().getId();
        var aliases = playerAliasRepository.findAllByUserId(userId);
        var response = buildAliasesMessage(aliases);
        telegramUserRepository.setState(message.getFrom().getId(), UserState.ALIASES);
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), response);
        sendMessage.setReplyMarkup(keyboards.getAliasesKeyboard());
        return sendMessage;
    }

    private String buildAliasesMessage(List<PlayerAlias> aliases) {
        StringBuilder response = new StringBuilder(BotMessageEnum.CURRENT_ALIASES.getMessage());
        for (PlayerAlias alias : aliases) {
            response.append(alias.getAlias()).append(" | ").append(alias.getPlayer().getName()).append("\n");
        }
        return response.toString();
    }

    @Override
    public boolean availableInAnyState() {
        return false;
    }
}
