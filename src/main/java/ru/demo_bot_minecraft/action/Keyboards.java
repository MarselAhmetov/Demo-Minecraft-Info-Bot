package ru.demo_bot_minecraft.action;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
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
        firstRow.add(RequestMessagesEnum.SERVER.getMessage());
        firstRow.add(RequestMessagesEnum.LOGS.getMessage());
        firstRow.add(RequestMessagesEnum.SUBSCRIPTION.getMessage());
        rows.add(firstRow);
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getSubscriptionsKeyboard(Long userId) {
        var subscriptions = subscriptionRepository.findAllByTelegramUserId(userId);
        KeyboardRow firstRow = new KeyboardRow();

        if (subscriptions.stream().noneMatch(sub -> sub.getType().equals(SubscriptionType.NEW_PLAYERS))) {
            firstRow.add(RequestMessagesEnum.NEW_PLAYERS_SUBSCRIPTION.getMessage());
        } else {
            firstRow.add(RequestMessagesEnum.CANCEL_NEW_PLAYERS_SUBSCRIPTION.getMessage());
        }
        if (subscriptions.stream().noneMatch(sub -> sub.getType().equals(SubscriptionType.PLAYERS_JOIN))) {
            firstRow.add(RequestMessagesEnum.PLAYERS_JOIN_SUBSCRIPTION.getMessage());
        } else {
            firstRow.add(RequestMessagesEnum.CANCEL_PLAYERS_JOIN_SUBSCRIPTION.getMessage());

        }
        firstRow.add(RequestMessagesEnum.MAIN_MENU.getMessage());
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(firstRow);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

}
