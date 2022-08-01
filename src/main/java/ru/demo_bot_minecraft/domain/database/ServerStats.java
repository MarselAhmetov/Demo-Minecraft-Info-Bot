package ru.demo_bot_minecraft.domain.database;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EqualsAndHashCode(callSuper = true)
public class ServerStats extends LongIdBaseEntity {

    String text;
    int maxPlayers;
    int onlinePlayers;
    @ManyToMany(fetch = FetchType.EAGER)
    List<Player> playersOnline;
    String name;
    String protocol;
}
