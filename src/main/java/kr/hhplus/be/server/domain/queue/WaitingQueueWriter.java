package kr.hhplus.be.server.domain.queue;

import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;

public interface WaitingQueueWriter {

    WaitingQueue save(WaitingQueue waitingQueue);

    int activateWaitingQueues(int activateSize);

    int expireWaitingQueues();

}
