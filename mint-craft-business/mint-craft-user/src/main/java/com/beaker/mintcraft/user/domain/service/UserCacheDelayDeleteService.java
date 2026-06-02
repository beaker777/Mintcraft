package com.beaker.mintcraft.user.domain.service;

import com.alicp.jetcache.Cache;
import com.beaker.mintcraft.user.domain.entity.User;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @Author beaker
 * @Date 2026/6/2 16:49
 * @Description 用户缓存延迟删除服务
 */
@Slf4j
@Service
public class UserCacheDelayDeleteService {

    private static ThreadFactory userCacheDelayProcessFactory = new ThreadFactoryBuilder()
            .setNameFormat("user-cache-delay-delete-pool-%d").build();

    private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(10, userCacheDelayProcessFactory);

    public void delayedCacheDelete(Cache idUserCache, User user) {
        scheduler.schedule(() -> {
            boolean idDeleteResult = idUserCache.remove(user.getId().toString());
            log.info("idUserCache removed, key = {}, result = {}", user.getId(), idDeleteResult);
        }, 3, TimeUnit.SECONDS);
    }
}
