package ru.demo_bot_minecraft.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.demo_bot_minecraft.domain.enums.PlayerReviveType;

@JsonSerialize
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRevivedRequest {
    @JsonProperty
    private String name;
    @JsonProperty
    private PlayerReviveType type;
}
