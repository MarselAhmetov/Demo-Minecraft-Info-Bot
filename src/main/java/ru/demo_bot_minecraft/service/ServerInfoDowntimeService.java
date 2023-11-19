package ru.demo_bot_minecraft.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.demo_bot_minecraft.domain.database.ServerInfoDowntime;
import ru.demo_bot_minecraft.repository.ServerInfoDowntimeRepository;
import ru.demo_bot_minecraft.util.DateUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServerInfoDowntimeService {

    private final ServerInfoDowntimeRepository serverInfoDowntimeRepository;

    public ServerInfoDowntime createDowntime(String error) {
        var downtime = ServerInfoDowntime.builder()
                .downtime(DateUtils.now())
                .error(error)
                .build();
        downtime = serverInfoDowntimeRepository.save(downtime);
        return downtime;
    }

    public Optional<ServerInfoDowntime> getCurrentDowntime() {
        return serverInfoDowntimeRepository.findByUptimeIsNull();
    }
}
