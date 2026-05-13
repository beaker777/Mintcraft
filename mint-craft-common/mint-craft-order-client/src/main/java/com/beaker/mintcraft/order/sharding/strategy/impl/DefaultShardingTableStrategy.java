package com.beaker.mintcraft.order.sharding.strategy.impl;

import com.beaker.mintcraft.order.sharding.strategy.ShardingTableStrategy;

/**
 * @Author beaker
 * @Date 2026/5/13 14:29
 * @Description 分表策略实现类
 */
public class DefaultShardingTableStrategy implements ShardingTableStrategy {

    @Override
    public int getTable(String externalId, int tableCount) {
        int hashCode = externalId.hashCode();

        // 将分表字段对表数量取模
        return (int) Math.abs((long) hashCode) % tableCount;
    }
}
