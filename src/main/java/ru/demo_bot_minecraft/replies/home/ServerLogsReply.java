package ru.demo_bot_minecraft.replies.home;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.database.PlayerAlias;
import ru.demo_bot_minecraft.domain.database.ServerEvent;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.PlayerAliasRepository;
import ru.demo_bot_minecraft.repository.ServerEventRepository;
import ru.demo_bot_minecraft.util.DateUtils;

import static ru.demo_bot_minecraft.util.ReplyUtils.messageEquals;

@Component
@RequiredArgsConstructor
public class ServerLogsReply implements Reply<Message> {

    private final ServerEventRepository serverEventRepository;
    private final PlayerAliasRepository playerAliasRepository;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return messageEquals(message, RequestMessagesEnum.LOGS);
    }

    @Override
    public BotApiMethod<?> getReply(Message message) {
        var userId = message.getFrom().getId();
        var aliases = playerAliasRepository.findAllByUserId(userId).stream()
                .collect(Collectors.toMap(p -> p.getPlayer().getName(), PlayerAlias::getAlias));
        var text = getText(aliases);
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(text)
                .replyMarkup(keyboards.getDefaultKeyboard())
                .build();
    }

    private String getText(Map<String, String> aliases) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(BotMessageEnum.LOGS.getMessage());
        var events = serverEventRepository.findAllByTimeBetweenOrderByTimeAsc(DateUtils.nowMinusHours(24L), DateUtils.now());
        for (ServerEvent event : events) {
            messageBuilder.append(event.getTime().format(DateTimeFormatter.ofPattern("dd.MM HH:mm"))).append(" ")
                    .append(aliases.getOrDefault(event.getPlayer().getName(), event.getPlayer().getName())).append(" ")
                    .append(event.getAction())
                    .append("\n");
        }
        return messageBuilder.toString();
    }

    @Override
    public UserState getRequiredUserState() {
        return UserState.DEFAULT;
    }

    @Override
    public boolean availableInAnyState() {
        return true;
    }
}
