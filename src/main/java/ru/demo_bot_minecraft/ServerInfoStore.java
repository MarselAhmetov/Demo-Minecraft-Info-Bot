package ru.demo_bot_minecraft;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.demo_bot_minecraft.domain.ServerEvent;
import ru.demo_bot_minecraft.domain.TimeInterval;

@Data
@Component
public class ServerInfoStore {
    private final Map<String, List<TimeInterval>> playingInfo = new HashMap<>();
    private final Map<String, Stack<ServerEvent>> playersEvents = new HashMap<>();
}
