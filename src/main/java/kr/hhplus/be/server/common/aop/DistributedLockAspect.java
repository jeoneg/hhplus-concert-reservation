package kr.hhplus.be.server.common.aop;

import kr.hhplus.be.server.common.aop.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import static kr.hhplus.be.server.common.aop.LockType.*;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonLockFacade redissonLockFacade;
    private final LettuceLockFacade lettuceLockFacade;

    @Around("@annotation(distributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String key = REDISSON_LOCK_PREFIX + generateKey(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());

        LockType lockType = distributedLock.lockType();

        if (lockType == SIMPLE_LOCK) {
            return redissonLockFacade.tryLock(
                key,
                distributedLock.waitTime(),
                distributedLock.leaseTime(),
                distributedLock.timeUnit(),
                joinPoint
            );
        }

        if (lockType == SPIN_LOCK) {
            return lettuceLockFacade.tryLock(
                key,
                distributedLock.leaseTime(),
                distributedLock.timeUnit(),
                joinPoint
            );
        }

        return null;
    }

    private Object generateKey(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(key).getValue(context, Object.class);
    }

}
