package ru.demo_bot_minecraft.util;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;

public class ReplyUtils {

    public static boolean messageEquals(Message message, RequestMessagesEnum messageEnum) {
        return message.getText().equalsIgnoreCase(messageEnum.getMessage());
    }

    public static boolean anyText(Message message) {
        return message.getText() != null;
    }
}
