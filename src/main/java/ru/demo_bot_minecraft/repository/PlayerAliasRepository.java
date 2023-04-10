package ru.demo_bot_minecraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.demo_bot_minecraft.domain.database.PlayerAlias;

import java.util.List;
import java.util.Optional;

public interface PlayerAliasRepository extends JpaRepository<PlayerAlias, Long> {

    List<PlayerAlias> findAllByUserId(Long userId);

    Optional<PlayerAlias> findByUserIdAndPlayerName(Long userId, String name);

    void deleteByUserIdAndAlias(Long userId, String alias);

    List<PlayerAlias> findAllByPlayerName(String name);
}
