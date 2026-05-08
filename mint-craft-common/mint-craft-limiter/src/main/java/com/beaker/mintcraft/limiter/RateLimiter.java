package com.beaker.mintcraft.limiter;

/**
 * @Author beaker
 * @Date 2026/5/8 19:37
 * @Description 限流服务
 */
public interface RateLimiter {


    /**
     * 判断一个key是否可以通过
     *
     * @param key 限流的key
     * @param limit 限流的数量
     * @param windowSize 窗口大小，单位为秒
     * @return
     */
    public Boolean tryAcquire(String key, int limit, int windowSize);
}
