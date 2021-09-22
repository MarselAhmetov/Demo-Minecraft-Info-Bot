package ru.demo_bot_minecraft.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.demo_bot_minecraft.action.ServerDefaultAction;
import ru.demo_bot_minecraft.action.ServerHistoryAction;
import ru.demo_bot_minecraft.action.ServerInfoAction;
import ru.demo_bot_minecraft.action.ServerLogsAction;


@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {


    private final ServerInfoAction infoAction;
    private final ServerLogsAction logsAction;
    private final ServerHistoryAction historyAction;
    private final ServerDefaultAction defaultAction;


    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (infoAction.getPredicate(update)) {
                execute(infoAction.makeAction(update));
            } else if (logsAction.getPredicate(update)) {
                execute(logsAction.makeAction(update));
            } else if (historyAction.getPredicate(update)) {
                execute(historyAction.makeAction(update));
            } else {
                execute(defaultAction.makeAction(update));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
