package kr.hhplus.be.server.common.aop;

import kr.hhplus.be.server.common.exception.LockAcquisitionFailedException;
import kr.hhplus.be.server.common.exception.LockInterruptedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockFacade {

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    public Object tryLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, ProceedingJoinPoint joinPoint) throws Throwable {
        RLock rLock = redissonClient.getLock(key);
        try {
            boolean isLocked = rLock.tryLock(waitTime, leaseTime, timeUnit);
            if (!isLocked) {
                throw new LockAcquisitionFailedException("락 획득 실패: key = " + key + ", thread = " + Thread.currentThread().getName());
            }

            log.info("Lock 획득 성공: {}", Thread.currentThread());
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockInterruptedException("락 획득 중단: key = " + key + ", thread = " + Thread.currentThread().getName(), e);
        } finally {
            try {
                rLock.unlock();
                log.info("Lock 정상 반환: key = {}, thread = {}", key, Thread.currentThread().getName());
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock [{}] [{}] [{}]", key, Thread.currentThread().getName(), e.getMessage());
            }
        }
    }

}
