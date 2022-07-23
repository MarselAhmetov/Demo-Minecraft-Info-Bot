package ru.demo_bot_minecraft.replies;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.repository.ServerEventRepository;

@Component
@RequiredArgsConstructor
public class ServerLogsReply implements Reply<Message> {

    private final ServerEventRepository serverEventRepository;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return message.getText().equals(RequestMessagesEnum.LOGS.getMessage());
    }

    @Override
    public BotApiMethod<?> getReply(Message message) {
        SendMessage sendMessage;
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Logs: \n");
        serverEventRepository.findAllByTimeBetweenOrderByTimeAsc(LocalDate.now(ZoneId.of("Europe/Moscow")).atStartOfDay(), LocalDateTime.now(
                ZoneId.of("Europe/Moscow")))
            .forEach(event -> messageBuilder.append(event.getTime().format(
                    DateTimeFormatter.ofPattern("dd.MM HH:mm"))).append(" ")
                .append(event.getPlayer().getName()).append(" ").append(event.getAction())
                .append("\n"));
        sendMessage = new SendMessage(message.getChatId().toString(), messageBuilder.toString());
        sendMessage.setReplyMarkup(keyboards.getDefaultKeyboard());
        return sendMessage;
    }

    @Override
    public BotState getState() {
        return BotState.DEFAULT;
    }

    @Override
    public boolean availableInAnyState() {
        return true;
    }
}
