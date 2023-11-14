package ru.demo_bot_minecraft.replies;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import ru.demo_bot_minecraft.domain.database.TelegramUserRole;
import ru.demo_bot_minecraft.domain.enums.UserState;

public interface Reply<T> {
    boolean predicate(T t);
    default UserState getRequiredUserState() {
        return UserState.DEFAULT;
    }
    BotApiMethod<?> getReply(T t);
    default boolean availableInAnyState() {
        return false;
    }
    default TelegramUserRole getRequiredRole() {
        return null;
    }
}
