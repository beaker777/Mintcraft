package com.beaker.mintcraft.api.order.response;

import com.beaker.mintcraft.base.response.BaseResponse;
import lombok.Data;

import static com.beaker.mintcraft.base.exception.biz.BizErrorCode.DUPLICATED;

/**
 * @Author beaker
 * @Date 2026/5/15 13:56
 * @Description 订单响应类
 */
@Data
public class OrderResponse extends BaseResponse {

    private String orderId;

    private String streamId;

    public static class OrderResponseBuilder {
        private String orderId;
        private String streamId;

        public OrderResponseBuilder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public OrderResponseBuilder streamId(String streamId) {
            this.streamId = streamId;
            return this;
        }

        public OrderResponse buildSuccess() {
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(orderId);
            orderResponse.setStreamId(streamId);
            orderResponse.setSuccess(true);

            return orderResponse;
        }

        public OrderResponse buildDuplicated() {
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(orderId);
            orderResponse.setStreamId(streamId);
            orderResponse.setSuccess(true);

            orderResponse.setResponseCode(DUPLICATED.getCode());
            orderResponse.setResponseMessage(DUPLICATED.getMessage());
            return orderResponse;
        }

        public OrderResponse buildFail(String code, String msg) {
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(orderId);
            orderResponse.setStreamId(streamId);
            orderResponse.setSuccess(false);

            orderResponse.setResponseCode(code);
            orderResponse.setResponseMessage(msg);
            return orderResponse;
        }
    }
}
