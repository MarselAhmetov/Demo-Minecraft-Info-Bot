package ru.demo_bot_minecraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.demo_bot_minecraft.domain.database.Player;

public interface PlayerRepository extends JpaRepository<Player, String> {

}
