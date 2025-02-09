package kr.hhplus.be.server.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@Aspect
public class TransactionLoggingAspect {

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void logTransactionStart(JoinPoint joinPoint) {
        log.info("TX 시작: {}", Thread.currentThread().getName());
    }

    @AfterReturning("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void logTransactionCommit() {
        log.info("TX 커밋 완료: {}", Thread.currentThread().getName());
    }

    @AfterThrowing("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void logTransactionRollback() {
        log.info("TX 롤백 발생: {}", Thread.currentThread().getName());
    }

}

