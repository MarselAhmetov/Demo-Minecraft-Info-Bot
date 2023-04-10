package ru.demo_bot_minecraft.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.demo_bot_minecraft.domain.database.ServerEvent;
import ru.demo_bot_minecraft.domain.dto.ServerAction;

public interface ServerEventRepository extends JpaRepository<ServerEvent, Long> {

    List<ServerEvent> findAllByTimeBetweenOrderByTimeAsc(LocalDateTime from, LocalDateTime to);

    boolean existsByPlayerNameAndTimeAfterAndAction(String name, LocalDateTime localDateTime, ServerAction serverAction);
}
