package ru.demo_bot_minecraft.replies.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.demo_bot_minecraft.domain.database.TelegramUser;
import ru.demo_bot_minecraft.domain.database.TelegramUserRole;
import ru.demo_bot_minecraft.domain.database.TelegramUserStatus;
import ru.demo_bot_minecraft.domain.enums.UserState;
import ru.demo_bot_minecraft.domain.enums.RequestMessagesEnum;
import ru.demo_bot_minecraft.replies.Reply;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.demo_bot_minecraft.util.ReplyUtils.messageEquals;

@Component
@RequiredArgsConstructor
public class UsersListReply implements Reply<Message> {

    private final TelegramUserRepository userRepository;

    @Override
    public boolean predicate(Message message) {
        return messageEquals(message, RequestMessagesEnum.USERS_LIST);
    }

    @Override
    @Transactional
    public BotApiMethod<?> getReply(Message message) {
        var users = userRepository.findAll();
        var text = buildText(users);
        return SendMessage.builder()
            .chatId(message.getChatId().toString())
            .text(text)
            .build();
    }

    private String buildText(List<TelegramUser> users) {
        Map<TelegramUserStatus, List<TelegramUser>> usersMap = users.stream()
                .collect(Collectors.groupingBy(TelegramUser::getStatus));
        StringBuilder text = new StringBuilder();
        usersMap.forEach((key, value) -> {
            text.append(key).append("\n");
            value.forEach(user ->
                    text.append(user.getUserName()).append(" ")
                            .append(user.getFirstName()).append(" ")
                            .append(user.getStatus()).append(" ")
                            .append(user.getRole()).append("\n"));
        });
        return text.toString();
    }

    @Override
    public UserState getRequiredUserState() {
        return UserState.ADMIN_SECTION;
    }


    @Override
    public TelegramUserRole getRequiredRole() {
        return TelegramUserRole.ADMIN;
    }
}
