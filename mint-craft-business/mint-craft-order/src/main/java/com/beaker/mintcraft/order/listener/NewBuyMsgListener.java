package com.beaker.mintcraft.order.listener;

import com.beaker.mintcraft.api.order.request.OrderCreateAndConfirmRequest;
import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.user.constant.UserType;
import com.beaker.mintcraft.mq.consumer.AbstractStreamConsumer;
import com.beaker.mintcraft.mq.param.MessageBody;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Consumer;

/**
 * @Author beaker
 * @Date 2026/5/16 20:21
 * @Description buy 方法消息监听类
 *
 * 本类会在 rocketmq.broker.check 为 false 时启用, 是单条消息的消费者.
 * 不强制依赖 RocketMQ , 但是如果不部署 MQ 会无法发送和消费信息.
 */
@Component
@Slf4j
@ConditionalOnProperty(value = "rocketmq.broker.check", havingValue = "false", matchIfMissing = true)
public class NewBuyMsgListener extends AbstractStreamConsumer {

    @Resource
    private OrderFacadeService orderFacadeService;

    public void doNewBuyExecute(OrderCreateRequest orderCreateRequest) {
        OrderCreateAndConfirmRequest orderCreateAndConfirmRequest = new OrderCreateAndConfirmRequest();
        BeanUtils.copyProperties(orderCreateRequest, orderCreateAndConfirmRequest);
        orderCreateAndConfirmRequest.setOperator(UserType.PLATFORM.name());
        orderCreateAndConfirmRequest.setOperatorType(UserType.PLATFORM);
        orderCreateAndConfirmRequest.setOperateTime(new Date());
        orderCreateAndConfirmRequest.setSyncDecreaseInventory(true);


    }
}
