package ru.demo_bot_minecraft.domain.database;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EqualsAndHashCode(callSuper = true)
public class ServerInfoDowntime extends LongIdBaseEntity {

    LocalDateTime downtime;
    LocalDateTime uptime;
    String error;
}
