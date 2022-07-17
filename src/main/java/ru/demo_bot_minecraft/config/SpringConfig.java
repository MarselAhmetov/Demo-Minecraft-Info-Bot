package ru.demo_bot_minecraft.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import ru.demo_bot_minecraft.bot.handler.CallbackQueryHandler;
import ru.demo_bot_minecraft.bot.handler.MessageHandler;
import ru.demo_bot_minecraft.bot.TelegramWebhookBot;

@Configuration
@AllArgsConstructor
public class SpringConfig {
    private final TelegramConfig telegramConfig;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(telegramConfig.getWebhookPath()).build();
    }

    @Bean
    public TelegramWebhookBot springWebhookBot(SetWebhook setWebhook,
                                         MessageHandler messageHandler,
                                         CallbackQueryHandler callbackQueryHandler) {
        TelegramWebhookBot bot = new TelegramWebhookBot(setWebhook, messageHandler, callbackQueryHandler);

        bot.setBotPath(telegramConfig.getWebhookPath());
        bot.setBotUsername(telegramConfig.getBotName());
        bot.setBotToken(telegramConfig.getBotToken());

        return bot;
    }
}