package kr.hhplus.be.server.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.concurrent.TimeUnit;

public interface DistributedLockStrategy {

    Object tryLock(String key, Long waitTime, Long leaseTime, TimeUnit timeUnit, ProceedingJoinPoint joinPoint) throws Throwable;

}
