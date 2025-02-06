package kr.hhplus.be.server.domain.queue;

import kr.hhplus.be.server.common.exception.BadRequestException;
import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.common.uuid.UUIDGenerator;
import kr.hhplus.be.server.domain.queue.entity.WaitingQueue;
import kr.hhplus.be.server.domain.queue.model.WaitingQueueInfo;
import kr.hhplus.be.server.domain.user.UserReader;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static kr.hhplus.be.server.common.exception.ErrorMessage.*;
import static kr.hhplus.be.server.domain.queue.entity.WaitingQueueStatus.WAITING;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class WaitingQueueService {

    private final UserReader userReader;
    private final WaitingQueueWriter waitingQueueWriter;
    private final WaitingQueueReader waitingQueueReader;
    private final UUIDGenerator uuidGenerator;

    @Transactional
    public WaitingQueueInfo.Create createWaitingQueue(WaitingQueueCommand.Create command) {
        command.validate();

        User user = userReader.findById(command.userId())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        String token = uuidGenerator.generate();
        WaitingQueue waitingQueue = WaitingQueue.of(user.getId(), token, WAITING);
        WaitingQueue savedWaitingQueue = waitingQueueWriter.save(waitingQueue);
        return WaitingQueueInfo.Create.from(savedWaitingQueue);
    }

    public WaitingQueueInfo.GetWaitingQueue getWaitingQueue(String token) {
        if (!StringUtils.hasText(token)) {
            throw new BadRequestException(QUEUE_TOKEN_VALUE_INVALID.getMessage());
        }

        WaitingQueue waitingQueue = waitingQueueReader.findByToken(token)
                .orElseThrow(() -> new NotFoundException(QUEUE_TOKEN_NOT_FOUND.getMessage()));
        return WaitingQueueInfo.GetWaitingQueue.of(waitingQueue);
    }

    public void checkActivatedWaitingQueue(String token) {
        waitingQueueReader.findByToken(token)
                .orElseThrow(() -> new NotFoundException(QUEUE_TOKEN_NOT_FOUND.getMessage()));
    }

    @Transactional
    public void expire(String token) {
        WaitingQueue waitingQueue = waitingQueueReader.findByToken(token)
                .orElseThrow(() -> new NotFoundException(QUEUE_TOKEN_NOT_FOUND.getMessage()));
        waitingQueueWriter.expire(waitingQueue);
    }

    @Transactional
    public int activateWaitingQueues(int activateSize) {
        return waitingQueueWriter.activateWaitingQueues(activateSize);
    }

}
