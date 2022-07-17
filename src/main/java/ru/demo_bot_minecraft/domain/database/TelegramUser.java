package ru.demo_bot_minecraft.domain.database;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.demo_bot_minecraft.domain.enums.BotState;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class TelegramUser {
    @Id
    Long id;
    String firstName;
    boolean isBot;
    String lastName;
    String userName;
    @Enumerated(EnumType.STRING)
    BotState botState;
    String minecraftName;
}
