package kr.hhplus.be.server.domain.queue;

import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;

import java.util.Optional;

public interface WaitingQueueReader {

    Optional<WaitingQueue> findByToken(String token);

}
