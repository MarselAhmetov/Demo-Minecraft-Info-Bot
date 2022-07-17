package ru.demo_bot_minecraft.bot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import ru.demo_bot_minecraft.bot.handler.CallbackQueryHandler;
import ru.demo_bot_minecraft.bot.handler.MessageHandler;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramWebhookBot extends SpringWebhookBot {
    String botPath;
    String botUsername;
    String botToken;

    MessageHandler messageHandler;
    CallbackQueryHandler callbackQueryHandler;

    public TelegramWebhookBot(SetWebhook setWebhook, MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler) {
        super(setWebhook);
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                return callbackQueryHandler.processCallbackQuery(update);
            } else {
                if (update.hasMessage()) {
                    return messageHandler.answerMessage(update);
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new SendMessage(update.getMessage().getChatId().toString(), "Error");
        } catch (Exception e) {
            e.printStackTrace();
            return new SendMessage(update.getMessage().getChatId().toString(), "Error");
        }
    }
}