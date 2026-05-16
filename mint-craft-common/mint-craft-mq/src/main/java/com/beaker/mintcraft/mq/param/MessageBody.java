package com.beaker.mintcraft.mq.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author beaker
 * @Date 2026/5/16 20:00
 * @Description 消息体
 */
@Data
@Accessors(chain = true)
public class MessageBody {

    /**
     * 幂等号
     */
    private String identifier;

    /**
     * 消息体
     */
    private String body;
}
