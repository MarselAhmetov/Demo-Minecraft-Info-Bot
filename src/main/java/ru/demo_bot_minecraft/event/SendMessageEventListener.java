package ru.demo_bot_minecraft.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.demo_bot_minecraft.bot.TelegramWebhookBot;

@Component
@RequiredArgsConstructor
public class SendMessageEventListener {

    private final TelegramWebhookBot telegramBot;

    @SneakyThrows
    @TransactionalEventListener(fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleContextStart(SendMessageEvent sendMessageEvent) {
        telegramBot.execute(new SendMessage(sendMessageEvent.getRecipient(), sendMessageEvent.getMessage()));
    }
}
