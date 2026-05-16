package com.beaker.mintcraft.mq.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author beaker
 * @Date 2026/5/16 20:09
 * @Description 信息
 */
@Data
@Accessors(chain = true)
public class Message {

    /**
     * 消息id
     */
    private String msgId;

    /**
     * 消息体
     */
    private String body;
}
