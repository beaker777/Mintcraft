package com.beaker.mintcraft.chain.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.beaker.mintcraft.chain.domain.constant.ChainOperateState;
import com.beaker.mintcraft.datasource.domain.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/5/25 20:47
 * @Description 链操作信息
 */
@Data
@TableName("chain_operate_info")
public class ChainOperateInfo extends BaseEntity {

    /**
     * 链类型
     */
    private String chainType;

    /**
     * 业务id
     */
    private String bizId;

    /**
     * 业务类型
     **/
    private String bizType;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 状态
     */
    private ChainOperateState state;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 成功时间
     */
    private Date succeedTime;

    /**
     * 入参
     */
    private String param;

    /**
     * 返回结果
     */
    private String result;

    /**
     * 外部业务id
     */
    private String outBizId;
}
