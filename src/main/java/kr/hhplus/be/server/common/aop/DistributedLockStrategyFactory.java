package kr.hhplus.be.server.common.aop;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistributedLockStrategyFactory {

    private final StrategyRedissonLock redissonLock;
    private final StrategyLettuceLock lettuceLock;

    public DistributedLockStrategy createStrategy(LockType lockType) {
        if (lockType == LockType.SIMPLE_LOCK) {
            return redissonLock;
        }
        if (lockType == LockType.SPIN_LOCK) {
            return lettuceLock;
        }
        throw new IllegalArgumentException("[ERROR] " + lockType + "에 해당하는 분산락 전략을 찾을 수 없습니다.");
    }

}
