package com.beaker.mintcraft.trade.listener;

import com.alibaba.fastjson2.JSON;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.base.response.SingleResponse;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author beaker
 * @Date 2026/5/19 17:25
 * @Description 库存扣减事务监听器
 */
@Component
public class InventoryDecreaseTransactionListener implements TransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(InventoryDecreaseTransactionListener.class);

    @Autowired
    private InventoryFacadeService inventoryFacadeService;

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        try {
            OrderCreateRequest orderCreateRequest = JSON.parseObject(JSON.parseObject(msg.getBody())
                            .getString("body"), OrderCreateRequest.class);
            InventoryRequest inventoryRequest = new InventoryRequest(orderCreateRequest);

            // 预扣减 Redis 库存
            SingleResponse<Boolean> response = inventoryFacadeService.decrease(inventoryRequest);

            if (response.getSuccess() && response.getData()) {
                return LocalTransactionState.COMMIT_MESSAGE;
            } else {
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        } catch (Exception e) {
            logger.error("executeLocalTransaction error, message = {}", msg, e);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        OrderCreateRequest orderCreateRequest = JSON.parseObject(JSON.parseObject(msg.getBody())
                .getString("body"), OrderCreateRequest.class);
        InventoryRequest inventoryRequest = new InventoryRequest(orderCreateRequest);

        // 获取 Redis 库存扣减记录
        SingleResponse<String> response = inventoryFacadeService.getInventoryDecreaseLog(inventoryRequest);

        // 如果存在扣减记录提交事务, 否则进行回滚
        return response.getSuccess() && response.getData() != null ? LocalTransactionState.COMMIT_MESSAGE : LocalTransactionState.ROLLBACK_MESSAGE;
    }
}
