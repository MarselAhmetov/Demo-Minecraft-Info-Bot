package ru.demo_bot_minecraft.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.demo_bot_minecraft.domain.database.TelegramUser;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;

import static ru.demo_bot_minecraft.domain.database.SubscriptionType.*;
import static ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum.*;

@Component
@RequiredArgsConstructor
public class Keyboards {

    private final SubscriptionRepository subscriptionRepository;

    public ReplyKeyboardMarkup getDefaultKeyboard() {
        KeyboardRow firstRow = new KeyboardRow();
        
        firstRow.add(SERVER.getMessage());
        firstRow.add(LOGS.getMessage());

        KeyboardRow secondRow = new KeyboardRow();

        secondRow.add(SUBSCRIPTION.getMessage());
        secondRow.add(PLAY_TIME.getMessage());

        KeyboardRow thirdRow = new KeyboardRow();

        thirdRow.add(SETTINGS.getMessage());
        thirdRow.add(ADMIN_SECTION.getMessage());

        return buildKeyboard(List.of(firstRow, secondRow, thirdRow));
    }

    public ReplyKeyboardMarkup getSubscriptionsKeyboard(Long userId) {
        var subscriptions = subscriptionRepository.findAllByTelegramUserId(userId);
        KeyboardRow firstRow = new KeyboardRow();

        if (subscriptions.stream().noneMatch(sub -> sub.getType().equals(NEW_PLAYERS))) {
            firstRow.add(NEW_PLAYERS_SUBSCRIPTION.getMessage());
        } else {
            firstRow.add(CANCEL_NEW_PLAYERS_SUBSCRIPTION.getMessage());
        }
        if (subscriptions.stream().noneMatch(sub -> sub.getType().equals(PLAYERS_JOIN))) {
            firstRow.add(PLAYERS_JOIN_SUBSCRIPTION.getMessage());
        } else {
            firstRow.add(CANCEL_PLAYERS_JOIN_SUBSCRIPTION.getMessage());
        }

        KeyboardRow secondRow = new KeyboardRow();

        if (subscriptions.stream().noneMatch(sub -> sub.getType().equals(DOWNTIME))) {
            secondRow.add(DOWNTIME_SUBSCRIPTION.getMessage());
        } else {
            secondRow.add(CANCEL_DOWNTIME_SUBSCRIPTION.getMessage());
        }
        if (subscriptions.stream().noneMatch(sub -> sub.getType().equals(PLAYERS_REVIVE))) {
            secondRow.add(PLAYERS_REVIVE_SUBSCRIPTION.getMessage());
        } else {
            secondRow.add(CANCEL_PLAYERS_REVIVE_SUBSCRIPTION.getMessage());
        }

        firstRow.add(MAIN_MENU.getMessage());
        return buildKeyboard(List.of(firstRow, secondRow));
    }

    public ReplyKeyboardMarkup getPlayTimeKeyboard() {
        KeyboardRow firstRow = new KeyboardRow();

        firstRow.add(TODAY.getMessage());
        firstRow.add(YESTERDAY.getMessage());
        firstRow.add(WEEK.getMessage());

        KeyboardRow secondRow = new KeyboardRow();

        secondRow.add(MONTH.getMessage());
        secondRow.add(ALL_TIME.getMessage());
        secondRow.add(MAIN_MENU.getMessage());

        return buildKeyboard(List.of(firstRow, secondRow));
    }

    public ReplyKeyboardMarkup getAdminSectionKeyboard() {
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(BAN_USER.getMessage());
        firstRow.add(UNBAN_USER.getMessage());
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(USERS_LIST.getMessage());
        secondRow.add(MAIN_MENU.getMessage());
        return buildKeyboard(List.of(firstRow, secondRow));
    }

    public ReplyKeyboardMarkup getSettingsKeyboard(TelegramUser user) {

        KeyboardRow firstRow = new KeyboardRow();
        if (user.getMinecraftName() == null) {
            firstRow.add(ADD_NICKNAME.getMessage());
        } else {
            firstRow.add(REMOVE_NICKNAME.getMessage() + " " + user.getMinecraftName());
        }
        firstRow.add(ALIASES.getMessage());
        firstRow.add(MAIN_MENU.getMessage());

        return buildKeyboard(List.of(firstRow));
    }

    public ReplyKeyboardMarkup getByState(TelegramUser user) {
        return switch (user.getState()) {
            case DEFAULT -> getDefaultKeyboard();
            case SUBSCRIPTION -> getSubscriptionsKeyboard(user.getId());
            case PLAY_TIME -> getPlayTimeKeyboard();
            case SETTINGS -> getSettingsKeyboard(user);
            case ADMIN_SECTION -> getAdminSectionKeyboard();
            case ALIASES -> getAliasesKeyboard();
            default -> null;
        };
    }
    
    private ReplyKeyboardMarkup buildKeyboard(List<KeyboardRow> rows) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getAliasesKeyboard() {
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(ADD_ALIAS.getMessage());
        firstRow.add(REMOVE_ALIAS.getMessage());
        firstRow.add(MAIN_MENU.getMessage());
        return buildKeyboard(List.of(firstRow));
    }
}
