package ru.demo_bot_minecraft.action;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Action {
    boolean getPredicate(Update update);
    BotApiMethod makeAction(Update update);
}
