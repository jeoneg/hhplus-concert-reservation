package kr.hhplus.be.server.interfaces.scheduler;

import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaitingQueueScheduler {

    private static final int ACTIVATE_SIZE = 100;

    private final WaitingQueueService waitingQueueService;

    @Scheduled(fixedDelay = 60000)
    public void activateWaitingQueue() {
        int activatedCount = waitingQueueService.activateWaitingQueues(ACTIVATE_SIZE);
        log.info("활성화 대기열 카운트: {}", activatedCount);
    }

}
