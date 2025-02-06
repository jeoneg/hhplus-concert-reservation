package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.domain.queue.WaitingQueueWriter;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

//@Repository
@RequiredArgsConstructor
public class WaitingQueueJpaWriter implements WaitingQueueWriter {

    private final WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Override
    public WaitingQueue save(WaitingQueue waitingQueue) {
        return waitingQueueJpaRepository.save(waitingQueue);
    }

    @Override
    public int activateWaitingQueues(int activateSize) {
        return waitingQueueJpaRepository.activateWaitingQueues(activateSize);
    }

    @Override
    public void expire(WaitingQueue waitingQueue) {

    }

//    @Override
//    public int expireWaitingQueues() {
//        return waitingQueueJpaRepository.expireWaitingQueues();
//    }

}
