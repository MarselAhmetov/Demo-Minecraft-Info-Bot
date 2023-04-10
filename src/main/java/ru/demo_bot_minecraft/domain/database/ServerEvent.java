package ru.demo_bot_minecraft.domain.database;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.demo_bot_minecraft.domain.dto.ServerAction;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EqualsAndHashCode(callSuper = true)
public class ServerEvent extends LongIdBaseEntity {

    LocalDateTime time;
    @OneToOne(fetch = FetchType.EAGER)
    Player player;
    @Enumerated(EnumType.STRING)
    ServerAction action;
}
