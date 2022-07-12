package ru.demo_bot_minecraft.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class HomeMenuAction implements Action {

    private final Keyboards keyboards;

    @Override
    public boolean getPredicate(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            if (message.hasText()) {
                var text = message.getText();
                if (text.equals("Home menu")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public BotApiMethod makeAction(Update update) {
        SendMessage message = new SendMessage(update.getMessage().getChatId().toString(), "Main menu");
        message.setReplyMarkup(keyboards.getDefaultKeyboard());
        return message;
    }
}
