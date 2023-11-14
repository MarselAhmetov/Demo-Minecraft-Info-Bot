package ru.demo_bot_minecraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.demo_bot_minecraft.domain.database.TelegramUser;
import ru.demo_bot_minecraft.domain.enums.UserState;

import java.util.Optional;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    @Query("update TelegramUser u set u.state = :newState where u.id = :id")
    @Modifying
    void setState(Long id, UserState newState);

    Optional<TelegramUser> findTelegramUserByUserName(String userName);
}
