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
import ru.demo_bot_minecraft.domain.enums.BotMessage;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;
import ru.demo_bot_minecraft.service.SubscriptionsService;

@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final Keyboards keyboards;
    private final TelegramUserRepository telegramUserRepository;
    private final StateDispatcher stateDispatcher;
    private final SubscriptionsService subscriptionsService;

    @Transactional
    public BotApiMethod<?> answerMessage(Update update) {
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        String inputText = message.getText();

        if (inputText == null) {
            throw new IllegalArgumentException();
        }

        var user = telegramUserRepository.findById(message.getFrom().getId())
                .orElseGet(() -> createNewUser(message));

        switch (user.getStatus()) {
            case BANNED:
                return SendMessage.builder()
                        .chatId(chatId)
                        .text(BotMessage.BANNED.getMessage())
                        .build();
            case WAITING_FOR_APPROVE:
                return SendMessage.builder()
                        .chatId(chatId)
                        .text(BotMessage.WAITING_FOR_APPROVE.getMessage())
                        .build();
            case BOT_BANNED_BY_USER:
                telegramUserRepository.setStatus(user.getId(), TelegramUserStatus.WAITING_FOR_APPROVE);
                subscriptionsService.sendUserWaitingForApproveMessage(user);
                return SendMessage.builder()
                        .chatId(chatId)
                        .text(BotMessage.WAITING_FOR_APPROVE.getMessage())
                        .build();
        }
        if (inputText.equals(RequestMessagesEnum.START.getMessage())) {
            return getStartMessage(message, user);
        }
        return stateDispatcher.dispatch(user, message, user.getState());
    }

    private TelegramUser createNewUser(Message message) {
        var user = telegramUserRepository.save(toTelegramUser(message));
        subscriptionsService.sendUserWaitingForApproveMessage(user);
        return user;
    }

    private static TelegramUser toTelegramUser(Message message) {
        return TelegramUser.builder()
                .id(message.getFrom().getId())
                .isBot(message.getFrom().getIsBot())
                .userName(message.getFrom().getUserName())
                .lastName(message.getFrom().getLastName())
                .firstName(message.getFrom().getFirstName())
                .state(UserState.DEFAULT)
                .build();
    }

    private SendMessage getStartMessage(Message message, TelegramUser user) {
        var chatId = message.getChatId().toString();
        SendMessage sendMessage = new SendMessage(chatId, BotMessage.WELCOME.getMessage());
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(keyboards.getDefaultKeyboard(user.getRole()));
        return sendMessage;
    }
}