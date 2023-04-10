package ru.demo_bot_minecraft.domain.database;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Entity
public class PlayerAlias {
    @Id
    String id;
    @ManyToOne(fetch = FetchType.LAZY)
    Player player;
    String alias;
    @ManyToOne(fetch = FetchType.LAZY)
    TelegramUser user;
}
