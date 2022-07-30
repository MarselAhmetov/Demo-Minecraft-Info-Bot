package ru.demo_bot_minecraft.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.demo_bot_minecraft.domain.database.Subscription;
import ru.demo_bot_minecraft.domain.database.SubscriptionType;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findAllByTelegramUserId(Long id);
    void deleteByTelegramUserIdAndType(Long id, SubscriptionType type);
    List<Subscription> findAllByType(SubscriptionType type);
}
