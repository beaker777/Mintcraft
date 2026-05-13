package com.beaker.mintcraft.order.sharding.strategy;

/**
 * @Author beaker
 * @Date 2026/5/13 14:28
 * @Description 分表策略
 */
public interface ShardingTableStrategy {

    /**
     * 获取分表结果
     *
     * @param externalId 外部id
     * @param tableCount 表数量
     * @return 分表结果
     */
    public int getTable(String externalId, int tableCount);
}
