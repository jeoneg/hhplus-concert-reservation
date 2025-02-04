package kr.hhplus.be.server.common.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import static kr.hhplus.be.server.common.exception.ErrorMessage.QUEUE_TOKEN_NOT_FOUND;

@Configuration
@RequiredArgsConstructor
public class WaitingQueueInterceptor implements HandlerInterceptor {

    private final WaitingQueueService waitingQueueService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Queue-Token");
        if (!StringUtils.hasText(token)) {
            throw new NotFoundException(QUEUE_TOKEN_NOT_FOUND.getMessage());
        }

        waitingQueueService.checkActivatedWaitingQueue(token);
        return true;
    }

}
