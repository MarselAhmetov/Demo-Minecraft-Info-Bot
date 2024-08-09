package ru.demo_bot_minecraft.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonSerialize
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerBuybackedRequest {
    @JsonProperty
    private String name;
}
