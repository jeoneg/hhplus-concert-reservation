package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.domain.queue.WaitingQueueReader;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

//@Repository
@RequiredArgsConstructor
public class WaitingQueueJpaReader implements WaitingQueueReader {

    private final WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Override
    public Optional<WaitingQueue> findByToken(String token) {
        return waitingQueueJpaRepository.findByToken(token);
    }


}
