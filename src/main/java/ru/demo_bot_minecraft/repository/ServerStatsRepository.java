package ru.demo_bot_minecraft.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.demo_bot_minecraft.domain.database.ServerStats;

public interface ServerStatsRepository extends JpaRepository<ServerStats, Long> {

    default ServerStats updateData(ServerStats serverStats) {
        var currentServerStats = findAll().stream().findFirst().orElse(ServerStats.builder().id(1L).build());
        serverStats.setId(currentServerStats.getId());
        return save(serverStats);
    }

    default Optional<ServerStats> getServerStats() {
        return findAll().stream().findFirst();
    }
}
