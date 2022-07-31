package ru.demo_bot_minecraft.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.demo_bot_minecraft.domain.database.TelegramUser;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

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

        KeyboardRow secondRow = new KeyboardRow();

        secondRow.add(RequestMessagesEnum.SUBSCRIPTION.getMessage());
        secondRow.add(RequestMessagesEnum.PLAY_TIME.getMessage());

        KeyboardRow thirdRow = new KeyboardRow();

        thirdRow.add(RequestMessagesEnum.SETTINGS.getMessage());
        rows.add(firstRow);
        rows.add(secondRow);
        rows.add(thirdRow);

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

        KeyboardRow secondRow = new KeyboardRow();

        if (subscriptions.stream().noneMatch(sub -> sub.getType().equals(SubscriptionType.DOWNTIME))) {
            secondRow.add(RequestMessagesEnum.DOWNTIME_SUBSCRIPTION.getMessage());
        } else {
            secondRow.add(RequestMessagesEnum.CANCEL_DOWNTIME_SUBSCRIPTION.getMessage());
        }

        firstRow.add(RequestMessagesEnum.MAIN_MENU.getMessage());
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(firstRow);
        rows.add(secondRow);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getPlayTimeKeyboard() {
        KeyboardRow firstRow = new KeyboardRow();

        firstRow.add(RequestMessagesEnum.TODAY.getMessage());
        firstRow.add(RequestMessagesEnum.YESTERDAY.getMessage());
        firstRow.add(RequestMessagesEnum.WEEK.getMessage());

        KeyboardRow secondRow = new KeyboardRow();

        secondRow.add(RequestMessagesEnum.MONTH.getMessage());
        secondRow.add(RequestMessagesEnum.ALL_TIME.getMessage());
        secondRow.add(RequestMessagesEnum.MAIN_MENU.getMessage());

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(firstRow);
        rows.add(secondRow);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getSettingsKeyboard(TelegramUser user) {

        KeyboardRow firstRow = new KeyboardRow();
        if (user.getMinecraftName() == null) {
            firstRow.add(RequestMessagesEnum.ADD_NICKNAME.getMessage());
        } else {
            firstRow.add(RequestMessagesEnum.REMOVE_NICKNAME.getMessage() + " " + user.getMinecraftName());
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

    public ReplyKeyboardMarkup getByState(TelegramUser user) {
        return switch (user.getBotState()) {
            case DEFAULT -> getDefaultKeyboard();
            case SUBSCRIPTION -> getSubscriptionsKeyboard(user.getId());
            case PLAY_TIME -> getPlayTimeKeyboard();
            case SETTINGS -> getSettingsKeyboard(user);
            default -> null;
        };
    }

}
