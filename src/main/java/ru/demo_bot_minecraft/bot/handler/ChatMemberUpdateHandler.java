package ru.demo_bot_minecraft.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import ru.demo_bot_minecraft.domain.database.TelegramUserStatus;
import ru.demo_bot_minecraft.repository.SubscriptionRepository;
import ru.demo_bot_minecraft.repository.TelegramUserRepository;

@Component
@RequiredArgsConstructor
public class ChatMemberUpdateHandler {

    private final static String KICKED_STATUS = "kicked";

    private final SubscriptionRepository subscriptionRepository;
    private final TelegramUserRepository userRepository;

    public void processChatMemberUpdate(ChatMemberUpdated chatMemberUpdated) {
        if (chatMemberUpdated.getNewChatMember().getStatus().equals(KICKED_STATUS)) {
            var userId = chatMemberUpdated.getFrom().getId();
            subscriptionRepository.deleteAllByTelegramUserId(userId);
            userRepository.setStatus(userId, TelegramUserStatus.BOT_BANNED_BY_USER);
        }
    }
}