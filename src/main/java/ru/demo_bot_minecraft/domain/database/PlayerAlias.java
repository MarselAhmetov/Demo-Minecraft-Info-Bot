package ru.demo_bot_minecraft.domain.database;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
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
public class PlayerAlias extends LongIdBaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    Player player;
    String alias;
    @ManyToOne(fetch = FetchType.LAZY)
    TelegramUser user;
}
