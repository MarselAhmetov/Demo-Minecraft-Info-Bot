package ru.demo_bot_minecraft.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@JsonSerialize
@Getter
@AllArgsConstructor
public class PlayerReviveRequest {
    @JsonProperty
    private String name;
    @JsonProperty
    private Map<String, Integer> materials;
}

