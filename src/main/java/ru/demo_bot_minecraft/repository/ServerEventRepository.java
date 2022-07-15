package ru.demo_bot_minecraft.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.demo_bot_minecraft.domain.database.ServerEvent;

public interface ServerEventRepository extends JpaRepository<ServerEvent, Long> {

    List<ServerEvent> findAllByTimeBetween(LocalDateTime from, LocalDateTime to);

}
