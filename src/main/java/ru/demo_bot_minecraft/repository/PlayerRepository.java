package ru.demo_bot_minecraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Optionals;
import ru.demo_bot_minecraft.domain.database.Player;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, String> {

    Optional<Player> findByName(String name);
}
