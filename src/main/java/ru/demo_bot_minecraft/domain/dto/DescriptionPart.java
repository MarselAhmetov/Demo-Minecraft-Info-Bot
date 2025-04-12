package ru.demo_bot_minecraft.domain.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class DescriptionPart {
    String text;
    String color;
    Boolean underlined;
    List<DescriptionPart> extra;
}
