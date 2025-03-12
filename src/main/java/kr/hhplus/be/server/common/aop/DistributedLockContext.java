package kr.hhplus.be.server.common.aop;

import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.concurrent.TimeUnit;

@Setter
public class DistributedLockContext {

    private DistributedLockStrategy strategy;

    public Object tryLock(String key, Long waitTime, Long leaseTime, TimeUnit timeUnit, ProceedingJoinPoint joinPoint) throws Throwable {
        return strategy.tryLock(key, waitTime, leaseTime, timeUnit, joinPoint);
    }

}
