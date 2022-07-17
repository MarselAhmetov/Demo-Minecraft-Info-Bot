package ru.demo_bot_minecraft.bot;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.demo_bot_minecraft.bot.handler.CallbackQueryHandler;
import ru.demo_bot_minecraft.bot.handler.MessageHandler;

@Setter
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;

    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackQueryHandler;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                 execute(callbackQueryHandler.processCallbackQuery(update));
            } else {
                if (update.hasMessage()) {
                    execute(messageHandler.answerMessage(update));
                }
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}