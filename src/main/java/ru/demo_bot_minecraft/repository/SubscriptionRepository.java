package ru.demo_bot_minecraft.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.demo_bot_minecraft.domain.database.Subscription;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findAllByTelegramUserId(Long id);
    void deleteByTelegramUserIdAndType(Long id, SubscriptionType type);
    @Query("from Subscription s where s.type = :type and s.telegramUser.status != 'BOT_BANNED_BY_USER'")
    List<Subscription> findAllByType(SubscriptionType type);
    void deleteAllByTelegramUserId(Long id);
}
