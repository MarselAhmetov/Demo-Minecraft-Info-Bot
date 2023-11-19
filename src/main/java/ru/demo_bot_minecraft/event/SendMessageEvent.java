package ru.demo_bot_minecraft.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Getter
public class SendMessageEvent extends ApplicationEvent {
    private final String message;
    private final ReplyKeyboardMarkup keyboard;
    private final String recipient;

    public SendMessageEvent(Object source, String message, String recipient) {
        super(source);
        this.message = message;
        this.keyboard = null;
        this.recipient = recipient;
    }

    public SendMessageEvent(Object source, String message, ReplyKeyboardMarkup keyboard, String recipient) {
        super(source);
        this.message = message;
        this.keyboard = keyboard;
        this.recipient = recipient;
    }
}
