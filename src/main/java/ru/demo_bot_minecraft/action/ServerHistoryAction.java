package ru.demo_bot_minecraft.action;

import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.demo_bot_minecraft.ServerInfoStore;

@Component
@RequiredArgsConstructor
public class ServerHistoryAction implements Action {

    private final ServerInfoStore serverInfoStore;
    private final Keyboards keyboards;

    @Override
    public boolean getPredicate(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            if (message.hasText()) {
                var text = message.getText();
                if (text.equals("History")) {
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
        messageBuilder.append("History: \n");
        serverInfoStore.getPlayingInfo().forEach((key, value) -> value.forEach(
                timeInterval -> messageBuilder.append(key).append(" ")
                        .append(timeInterval.getStart().format(DateTimeFormatter.ofPattern("dd.MM HH:mm:ss"))).append(" ")
                        .append(timeInterval.getDuration().toHours()).append("h ")
                        .append(timeInterval.getDuration().toMinutesPart()).append("m ")
                        .append(timeInterval.getDuration().toSecondsPart()).append("s")
                        .append("\n")));
        message = new SendMessage(update.getMessage().getChatId().toString(),
                messageBuilder.toString());
        message.setReplyMarkup(keyboards.getDefaultKeyboard());
        return message;
    }
}
