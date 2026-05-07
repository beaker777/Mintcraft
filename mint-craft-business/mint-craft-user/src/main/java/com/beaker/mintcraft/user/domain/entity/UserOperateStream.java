package com.beaker.mintcraft.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.beaker.mintcraft.datasource.domain.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/5/7 15:23
 * @Description 用户操作流水
 */
@Data
@TableName("user_operate_stream")
public class UserOperateStream extends BaseEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 操作参数
     */
    private String param;

    /**
     * 扩展字段
     */
    private String extendInfo;
}