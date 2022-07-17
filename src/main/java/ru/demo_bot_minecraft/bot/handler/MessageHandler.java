package ru.demo_bot_minecraft.bot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.demo_bot_minecraft.action.Keyboards;
import ru.demo_bot_minecraft.dispatcher.StateDispatcher;
import ru.demo_bot_minecraft.domain.database.TelegramUser;
import ru.demo_bot_minecraft.domain.enums.BotState;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {

    Keyboards keyboards;
    TelegramUserRepository telegramUserRepository;
    StateDispatcher stateDispatcher;
    public BotApiMethod<?> answerMessage(Update update) {
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        String inputText = message.getText();

        if (inputText == null) {
            throw new IllegalArgumentException();
        }
        if (inputText.equals("/start")) {
            return getStartMessage(chatId);
        }
        var state = telegramUserRepository.findById(message.getFrom().getId())
            .map(TelegramUser::getBotState).orElse(BotState.DEFAULT);
        return stateDispatcher.dispatch(message, state);
    }

    private SendMessage getStartMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Hello there!");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(keyboards.getDefaultKeyboard());
        return sendMessage;
    }
}