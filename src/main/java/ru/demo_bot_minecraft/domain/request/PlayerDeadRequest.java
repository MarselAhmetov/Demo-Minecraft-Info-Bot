package ru.demo_bot_minecraft.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@JsonSerialize
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDeadRequest {
    @JsonProperty
    private String name;
    @JsonProperty
    private Map<String, Integer> materials;
}

