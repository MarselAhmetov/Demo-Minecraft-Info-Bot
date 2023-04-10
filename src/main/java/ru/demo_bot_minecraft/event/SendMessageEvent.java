package ru.demo_bot_minecraft.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SendMessageEvent extends ApplicationEvent {
    private final String message;
    private final String recipient;

    public SendMessageEvent(Object source, String message, String recipient) {
        super(source);
        this.message = message;
        this.recipient = recipient;
    }
}
