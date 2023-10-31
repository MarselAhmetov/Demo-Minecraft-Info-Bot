package ru.demo_bot_minecraft.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.demo_bot_minecraft.bot.TelegramBot;

@Component
@RequiredArgsConstructor
public class SendMessageEventListener {

    private final TelegramBot telegramBot;

    @EventListener
    public void handleContextStart(SendMessageEvent sendMessageEvent) {
        try {
            telegramBot.execute(new SendMessage(sendMessageEvent.getRecipient(), sendMessageEvent.getMessage()));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
