package ru.demo_bot_minecraft.bot;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.demo_bot_minecraft.action.Action;
import ru.demo_bot_minecraft.action.Keyboards;
import ru.demo_bot_minecraft.domain.database.TelegramUser;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;


@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramUserRepository telegramUserRepository;
    private final List<Action> actions;
    private final Keyboards keyboards;

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
            if (haveAccess(update.getMessage().getFrom())) {
                saveOrUpdate(update.getMessage().getFrom());
                boolean didAnyAction = false;
                for (Action action : actions) {
                    if (action.getPredicate(update)) {
                        execute(action.makeAction(update));
                        didAnyAction = true;
                    }
                }
                if (!didAnyAction) {
                    execute(defaultAction(update));
                }
            } else {
                execute(accessDeniedAction(update));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private BotApiMethod defaultAction(Update update) {
        SendMessage message;
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Пока, что могу только присылать информацию по серверу по запросам: \n\"Server\"\n\"Logs\"");
        message = new SendMessage(update.getMessage().getChatId().toString(), messageBuilder.toString());
        message.setReplyMarkup(keyboards.getDefaultKeyboard());
        return message;
    }

    private BotApiMethod accessDeniedAction(Update update) {
        SendMessage message;
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("У вас пока нет доступа к этому боту");
        message = new SendMessage(update.getMessage().getChatId().toString(), messageBuilder.toString());
        return message;
    }

    private boolean haveAccess(User user) {
        return true;
    }

    private TelegramUser saveOrUpdate(User user) {
        var telegramUser = TelegramUser.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .userName(user.getUserName())
            .isBot(user.getIsBot())
            .build();
        if (!telegramUserRepository.existsById(user.getId())) {
            telegramUserRepository.save(telegramUser);
        }
        return telegramUser;
    }
}
