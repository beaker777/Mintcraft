package com.beaker.mintcraft.limiter;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

/**
 * @Author beaker
 * @Date 2026/5/8 20:16
 * @Description 滑动窗口限流
 */
public class SlidingWindowRateLimiter implements RateLimiter {

    private RedissonClient redissonClient;

    public static final String LIMIT_KEY_PREFIX = "mintcraft:limit:";

    public SlidingWindowRateLimiter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Boolean tryAcquire(String key, int limit, int windowSize) {
        RRateLimiter rRateLimiter = redissonClient.getRateLimiter(LIMIT_KEY_PREFIX + key);

        // 判断是否第一次创建限流器, 如果是需要指定规则
        if (!rRateLimiter.isExists()) {
            rRateLimiter.trySetRate(RateType.OVERALL, limit, windowSize, RateIntervalUnit.SECONDS);
        }

        // 尝试获取一个令牌
        return rRateLimiter.tryAcquire();
    }
}
