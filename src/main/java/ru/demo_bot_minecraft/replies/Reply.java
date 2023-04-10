package ru.demo_bot_minecraft.replies;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import ru.demo_bot_minecraft.domain.enums.BotState;

public interface Reply<T> {
    boolean predicate(T t);
    BotState getState();
    BotApiMethod<?> getReply(T t);
    boolean availableInAnyState();
}
