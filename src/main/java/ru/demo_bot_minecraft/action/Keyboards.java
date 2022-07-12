package ru.demo_bot_minecraft.action;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.demo_bot_minecraft.domain.database.Subscription;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class Keyboards {

    private final SubscriptionRepository subscriptionRepository;

    public ReplyKeyboardMarkup getDefaultKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        firstRow.add("Server");
        firstRow.add("Logs");
        firstRow.add("Subscriptions");
        rows.add(firstRow);
        replyKeyboardMarkup.setKeyboard(rows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getSubscriptionsKeyboard(List<Subscription> subscriptions) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        if (subscriptions.stream().noneMatch(sub -> sub.getType().equals(SubscriptionType.NEW_PLAYERS))) {
            firstRow.add("New Players");
        } else {
            firstRow.add("Cancel New Players");

        }
        if (subscriptions.stream().noneMatch(sub -> sub.getType().equals(SubscriptionType.PLAYERS_JOIN))) {
            firstRow.add("Players join");
        } else {
            firstRow.add("Cancel Players join");

        }
        firstRow.add("Home menu");
        rows.add(firstRow);
        replyKeyboardMarkup.setKeyboard(rows);
        return replyKeyboardMarkup;
    }

}
