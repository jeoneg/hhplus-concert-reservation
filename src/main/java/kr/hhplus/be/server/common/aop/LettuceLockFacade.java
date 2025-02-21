package kr.hhplus.be.server.common.aop;

import kr.hhplus.be.server.infrastructure.lock.RedisLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LettuceLockFacade {

    private final RedisLockRepository redisLockRepository;
    private final AopForTransaction aopForTransaction;

    public Object tryLock(String key, long leaseTime, TimeUnit timeUnit, ProceedingJoinPoint joinPoint) throws Throwable {
        while (!redisLockRepository.tryLock(key, leaseTime, timeUnit)) {
            log.info("Lock 획득 실패: key = {}, thread = {}", key, Thread.currentThread().getName());
            Thread.sleep(1000);
        }

        try {
            log.info("Lock 획득 성공: key = {}, thread = {}", key, Thread.currentThread().getName());
            return aopForTransaction.proceed(joinPoint);
        } finally {
            redisLockRepository.unlock(key);
            log.info("Lock 정상 반환: key = {}, thread = {}", key, Thread.currentThread().getName());
        }
    }

}
