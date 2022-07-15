package ru.demo_bot_minecraft.action;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.demo_bot_minecraft.repository.ServerEventRepository;

@Component
@RequiredArgsConstructor
public class ServerLogsAction implements Action {

    private final ServerEventRepository serverEventRepository;
    private final Keyboards keyboards;

    @Override
    public boolean getPredicate(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            if (message.hasText()) {
                var text = message.getText();
                if (text.equals("Logs")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public BotApiMethod makeAction(Update update) {
        SendMessage message;
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Logs: \n");
        serverEventRepository.findAllByTimeBetween(LocalDate.now().atStartOfDay(), LocalDateTime.now(
                ZoneId.of("Europe/Moscow")))
            .forEach(event -> messageBuilder.append(event.getTime().format(
                    DateTimeFormatter.ofPattern("dd.MM HH:mm"))).append(" ")
                .append(event.getPlayer().getName()).append(" ").append(event.getAction())
                .append("\n"));
        message = new SendMessage(update.getMessage().getChatId().toString(), messageBuilder.toString());
        message.setReplyMarkup(keyboards.getDefaultKeyboard());
        return message;
    }
}
