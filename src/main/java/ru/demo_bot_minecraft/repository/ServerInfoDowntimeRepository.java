package ru.demo_bot_minecraft.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.demo_bot_minecraft.domain.database.ServerInfoDowntime;

public interface ServerInfoDowntimeRepository extends JpaRepository<ServerInfoDowntime, String> {
    Optional<ServerInfoDowntime> findByUptimeIsNull();
}
