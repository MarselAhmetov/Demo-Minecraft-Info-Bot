package ru.demo_bot_minecraft.replies.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.Keyboards;
import ru.demo_bot_minecraft.domain.database.TelegramUser;
import ru.demo_bot_minecraft.domain.database.TelegramUserRole;
import ru.demo_bot_minecraft.domain.database.TelegramUserStatus;
import ru.demo_bot_minecraft.domain.enums.BotMessage;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.event.SendMessageEvent;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

import static ru.demo_bot_minecraft.util.ReplyUtils.anyText;

@Component
@RequiredArgsConstructor
public class UserToApproveReply implements Reply<Message> {

    private final TelegramUserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Keyboards keyboards;

    @Override
    public boolean predicate(Message message) {
        return anyText(message);
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        userRepository.setState(message.getFrom().getId(), UserState.ADMIN_SECTION);
        var username = message.getText();
        var userOptional = userRepository.findTelegramUserByUserName(username);
        return userOptional.map(
                it -> {
                    approveUser(it);
                    return SendMessage.builder()
                            .chatId(message.getChatId().toString())
                            .text(BotMessage.USER_APPROVED.getMessage().formatted(it.getUserName()))
                            .build();
                }
        ).orElseGet(() -> SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(BotMessage.USER_NOT_FOUND.getMessage().formatted(username))
                .build());
    }

    private void approveUser(TelegramUser user) {
        user.setStatus(TelegramUserStatus.APPROVED);
        sendMessageToApprovedUser(user);
        userRepository.save(user);
    }

    private void sendMessageToApprovedUser(TelegramUser user) {
        SendMessageEvent event = new SendMessageEvent(this,
                BotMessage.YOU_ARE_APPROVED.getMessage(),
                keyboards.getDefaultKeyboard(user.getRole()),
                user.getId().toString());
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public UserState getRequiredUserState() {
        return UserState.APPROVE_USER;
    }

    @Override
    public TelegramUserRole getRequiredRole() {
        return TelegramUserRole.ADMIN;
    }
}
