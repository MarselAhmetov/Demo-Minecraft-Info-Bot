package ru.demo_bot_minecraft.event;

import org.springframework.context.ApplicationEvent;

public class SendMessageEvent extends ApplicationEvent {
    private final String message;
    private final String recipient;

    public SendMessageEvent(Object source, String message, String recipient) {
        super(source);
        this.message = message;
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getRecipient() {
        return recipient;
    }
}
