package ru.demo_bot_minecraft.bot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CallbackQueryHandler {

    public BotApiMethod<?> processCallbackQuery(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        final String chatId = callbackQuery.getMessage().getChatId().toString();
        String data = callbackQuery.getData();
        return handleDefault(chatId, data);
    }

    private SendMessage handleDefault(String chatId, String data) {
            return new SendMessage(chatId, "We received " + data + " but do not know how to handle it yet");
    }

}