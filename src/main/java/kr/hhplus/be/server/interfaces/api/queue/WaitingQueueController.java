package kr.hhplus.be.server.interfaces.api.queue;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import kr.hhplus.be.server.domain.queue.model.WaitingQueueInfo;
import kr.hhplus.be.server.interfaces.api.queue.request.WaitingQueueRequest;
import kr.hhplus.be.server.interfaces.api.queue.response.WaitingQueueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/waiting-queues")
@RequiredArgsConstructor
public class WaitingQueueController {

    private final WaitingQueueService waitingQueueService;

    @PostMapping
    public ResponseEntity<WaitingQueueResponse.Create> createWaitingQueue(
            @Valid @RequestBody WaitingQueueRequest.Create request
    ) {
        WaitingQueueInfo.Create waitingQueue = waitingQueueService.createWaitingQueue(request.toCommand());
        return ResponseEntity.status(CREATED).body(WaitingQueueResponse.Create.from(waitingQueue));
    }

    @GetMapping
    public ResponseEntity<WaitingQueueResponse.GetWaitingQueue> getWaitingQueue(
            @RequestHeader("Queue-Token") String token
    ) {
        WaitingQueueInfo.GetWaitingQueue waitingQueue = waitingQueueService.getWaitingQueue(token);
        return ResponseEntity.ok(WaitingQueueResponse.GetWaitingQueue.from(waitingQueue));
    }

}
