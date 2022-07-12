package ru.demo_bot_minecraft.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionsCancelAction implements Action {

    private final SubscriptionRepository subscriptionRepository;
    private final Keyboards keyboards;

    @Override
    public boolean getPredicate(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            if (message.hasText()) {
                var text = message.getText();
                if (text.equals("Cancel New Players") || text.equals("Cancel Players join")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional
    public BotApiMethod makeAction(Update update) {
        var text = update.getMessage().getText();
        SendMessage message;
        StringBuilder messageBuilder = new StringBuilder();
        if (text.equals("Cancel New Players")) {
            subscriptionRepository.deleteByTelegramUserIdAndType(update.getMessage().getFrom().getId(), SubscriptionType.NEW_PLAYERS);
            messageBuilder.append("New Players subscription canceled");
        }
        if (text.equals("Cancel Players join")) {
            subscriptionRepository.deleteByTelegramUserIdAndType(update.getMessage().getFrom().getId(), SubscriptionType.PLAYERS_JOIN);
            messageBuilder.append("Players join subscription canceled");
        }
        message = new SendMessage(update.getMessage().getChatId().toString(), messageBuilder.toString());
        message.setReplyMarkup(keyboards.getDefaultKeyboard());
        return message;
    }
}
