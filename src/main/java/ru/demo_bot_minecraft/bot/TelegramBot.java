package ru.demo_bot_minecraft.bot;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.demo_bot_minecraft.bot.handler.CallbackQueryHandler;
import ru.demo_bot_minecraft.bot.handler.ChatMemberUpdateHandler;
import ru.demo_bot_minecraft.bot.handler.MessageHandler;

@Setter
@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botUsername;

    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final ChatMemberUpdateHandler chatMemberUpdateHandler;

    public TelegramBot(
            @Value("${bot.token}") String botToken,
            MessageHandler messageHandler,
            CallbackQueryHandler callbackQueryHandler,
            ChatMemberUpdateHandler chatMemberUpdateHandler
    ) {
        super(botToken);
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.chatMemberUpdateHandler = chatMemberUpdateHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                execute(callbackQueryHandler.processCallbackQuery(update));
            } else if (update.hasMessage()) {
                execute(messageHandler.answerMessage(update));
            } else if (update.hasMyChatMember()) {
                chatMemberUpdateHandler.processChatMemberUpdate(update.getMyChatMember());
            }
        } catch (TelegramApiException e) {
            log.error("Error while processing update: {}", update, e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}