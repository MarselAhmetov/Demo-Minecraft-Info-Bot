package ru.demo_bot_minecraft.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.demo_bot_minecraft.service.SubscriptionsService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionsJob {

    private final SubscriptionsService subscriptionsService;

    @Scheduled(cron = "0 0 12 * * ?")
    @Transactional
    public void sendUserWaitingForApproveMessage() {
        log.info("Start sending message to admin about user in waiting for approve status");
        subscriptionsService.sendUsersWaitingForApproveMessage();
    }
}
