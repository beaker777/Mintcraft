package com.beaker.mintcraft.order.sharding.id;

import cn.hutool.core.util.IdUtil;
import com.beaker.mintcraft.api.common.constant.BusinessCode;
import com.beaker.mintcraft.order.sharding.strategy.impl.DefaultShardingTableStrategy;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author beaker
 * @Date 2026/5/13 14:27
 * @Description 分布式 ID
 */
public class DistributeID {

    /**
     * 系统标识码
     */
    private String businessCode;

    /**
     * 表下标
     */
    private String table;

    /**
     * 序列号
     */
    private String seq;

    /**
     * 分表策略
     */
    private static DefaultShardingTableStrategy shardingTableStrategy = new DefaultShardingTableStrategy();

    @Override
    public String toString() {
        return this.businessCode + this.seq + this.table;
    }

    /**
     * 根据分布式 id 获取 DistributeID 对象
     *
     * @param id
     * @return
     */
    public static DistributeID valueOf(String id) {
        DistributeID distributeId = new DistributeID();
        distributeId.businessCode = id.substring(0, 2);
        distributeId.seq = id.substring(2, id.length() - 4);
        distributeId.table = id.substring(id.length() - 4, id.length());

        return distributeId;
    }

    /**
     * 利用雪花算法生成一个唯一ID
     */
    public static String generateWithSnowflake(BusinessCode businessCode, long workerId, String externalId) {
        // 使用 snowflake 算法生成一个 seq
        long id = IdUtil.getSnowflake(workerId).nextId();

        // 根据 id 生成全局唯一的分布式 id
        return generate(businessCode, externalId, id);
    }

    /**
     * 生成一个唯一 ID 10(业务码) 1769649671860822016 (sequence) 1023 (分表)
     */
    public static String generate(BusinessCode businessCode, String externalId, Long sequenceNumber) {
        // 获取对象
        DistributeID distributeId = create(businessCode, externalId, sequenceNumber);

        // 拼接字符串
        return distributeId.businessCode + distributeId.seq + distributeId.table;
    }

    public static DistributeID create(BusinessCode businessCode, String externalId, Long sequenceNumber) {
        // 创建 distributeId 对象
        DistributeID distributeId = new DistributeID();
        distributeId.businessCode = businessCode.getCodeString();
        String table = String.valueOf(shardingTableStrategy.getTable(externalId, businessCode.tableCount()));
        distributeId.table = StringUtils.leftPad(table, 4, "0");
        distributeId.seq = String.valueOf(sequenceNumber);

        return distributeId;
    }

    /**
     * 根据 distributeId 获取分表
     *
     * @param distributeId
     * @return
     */
    public static String getShardingTable(DistributeID distributeId){
        return distributeId.table;
    }

    /**
     * 根据 id 获取分表
     *
     * @param distributeId
     * @return
     */
    public static String getShardingTable(String distributeId){
        return getShardingTable(valueOf(distributeId));
    }

    /**
     * 根据分表字段和表数获取对象
     *
     * @param externalId
     * @param tableCount
     * @return
     */
    public static String getShardingTable(String externalId, int tableCount) {
        return StringUtils.leftPad(
                String.valueOf(shardingTableStrategy.getTable(externalId, tableCount)), 4, "0");
    }
}
