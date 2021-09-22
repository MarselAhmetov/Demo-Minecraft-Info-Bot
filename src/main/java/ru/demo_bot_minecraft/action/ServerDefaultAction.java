package ru.demo_bot_minecraft.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ServerDefaultAction implements Action {

    private final Keyboards keyboards;

    @Override
    public boolean getPredicate(Update update) {
        return true;
    }

    @Override
    public BotApiMethod makeAction(Update update) {
        SendMessage message;
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Пока, что могу только присылать информацию по серверу по запросам: \n\"Server\"\n\"Logs\"\n\"History\"");
        message = new SendMessage(update.getMessage().getChatId().toString(), messageBuilder.toString());
        message.setReplyMarkup(keyboards.getDefaultKeyboard());
        return message;
    }
}
