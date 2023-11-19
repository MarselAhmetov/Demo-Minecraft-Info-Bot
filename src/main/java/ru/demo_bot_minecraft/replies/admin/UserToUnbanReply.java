package ru.demo_bot_minecraft.replies.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.database.TelegramUserRole;
import ru.demo_bot_minecraft.domain.database.TelegramUserStatus;
import ru.demo_bot_minecraft.domain.enums.BotMessage;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

import static ru.demo_bot_minecraft.util.ReplyUtils.anyText;

@Component
@RequiredArgsConstructor
public class UserToUnbanReply implements Reply<Message> {

    private final TelegramUserRepository userRepository;

    @Override
    public boolean predicate(Message message) {
        return anyText(message);
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        userRepository.setState(message.getFrom().getId(), UserState.ADMIN_SECTION);
        var username = message.getText();
        var user = userRepository.findTelegramUserByUserName(username);
        if (user.isPresent()) {
            var telegramUser = user.get();
            userRepository.setStatus(telegramUser.getId(), TelegramUserStatus.APPROVED);
            return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(BotMessage.USER_UNBANNED.getMessage().formatted(telegramUser.getUserName()))
                .build();
        } else {
            return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(BotMessage.USER_NOT_FOUND.getMessage().formatted(username))
                .build();
        }
    }

    @Override
    public UserState getRequiredUserState() {
        return UserState.UNBAN_USER;
    }

    @Override
    public TelegramUserRole getRequiredRole() {
        return TelegramUserRole.ADMIN;
    }
}
