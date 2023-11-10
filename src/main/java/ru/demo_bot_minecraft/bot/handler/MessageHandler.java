package ru.demo_bot_minecraft.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.demo_bot_minecraft.dispatcher.StateDispatcher;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.database.TelegramUser;
import ru.demo_bot_minecraft.domain.database.TelegramUserStatus;
import ru.demo_bot_minecraft.domain.enums.BotMessageEnum;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final Keyboards keyboards;
    private final TelegramUserRepository telegramUserRepository;
    private final StateDispatcher stateDispatcher;
    @Transactional
    public BotApiMethod<?> answerMessage(Update update) {
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        String inputText = message.getText();

        if (inputText == null) {
            throw new IllegalArgumentException();
        }

        var user = telegramUserRepository.findById(message.getFrom().getId())
                .orElseGet(() -> telegramUserRepository.save(TelegramUser.builder()
                        .id(message.getFrom().getId())
                        .isBot(message.getFrom().getIsBot())
                        .userName(message.getFrom().getUserName())
                        .lastName(message.getFrom().getLastName())
                        .firstName(message.getFrom().getFirstName())
                        .state(UserState.DEFAULT)
                        .build())
                );
        if (user.getStatus().equals(TelegramUserStatus.BANNED)) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(BotMessageEnum.BANNED.getMessage())
                    .build();
        }
        if (user.getStatus().equals(TelegramUserStatus.WAITING_FOR_APPROVE)) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(BotMessageEnum.WAITING_FOR_APPROVE.getMessage())
                    .build();
        }
        if (inputText.equals(RequestMessagesEnum.START.getMessage())) {
            return getStartMessage(chatId);
        }
        return stateDispatcher.dispatch(user, message, user.getState());
    }

    private SendMessage getStartMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.WELCOME.getMessage());
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(keyboards.getDefaultKeyboard());
        return sendMessage;
    }
}