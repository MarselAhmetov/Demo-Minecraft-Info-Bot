package ru.demo_bot_minecraft.domain.dto;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServerStatsResponse {
    @Builder.Default
    Optional<ServerStats> serverStats = Optional.empty();
    String error;
}
