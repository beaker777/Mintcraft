package com.beaker.mintcraft.notice.domain.entity;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.TableName;
import com.beaker.mintcraft.api.notice.constant.NoticeState;
import com.beaker.mintcraft.api.notice.constant.NoticeType;
import com.beaker.mintcraft.datasource.domain.entity.BaseEntity;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @Author beaker
 * @Date 2026/5/8 14:27
 * @Description 通知实体类
 */
@Data
@Builder
@TableName("notice")
public class Notice extends BaseEntity {

    /**
     * 通知标题
     */
    private String noticeTitle;

    /**
     * 通知内容
     */
    private String noticeContent;

    /**
     * 通知类型
     */
    private NoticeType noticeType;

    /**
     * 发送成功时间
     */
    private Date sendSuccessTime;

    /**
     * 接收地址
     */
    private String targetAddress;

    /**
     * 状态
     */
    private NoticeState state;

    /**
     * 重试次数
     */
    private int retryTimes;

    /**
     * 扩展信息
     */
    private String extendInfo;

    public void addExtendInfo(String key, String value) {
        Map<String, String> extendInfoMap;
        if (extendInfo == null) {
            extendInfoMap = Maps.newHashMapWithExpectedSize(1);
        } else {
            extendInfoMap = JSON.parseObject(extendInfo, Map.class);
        }

        extendInfoMap.put(key, value);
        extendInfo = JSON.toJSONString(extendInfoMap);
    }
}
