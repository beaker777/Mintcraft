package com.beaker.mintcraft.order.domain.entity.statemachine;

import com.beaker.mintcraft.api.order.constant.TradeOrderEvent;
import com.beaker.mintcraft.api.order.constant.TradeOrderState;
import com.beaker.mintcraft.base.statemachine.BaseStateMachine;

/**
 * @Author beaker
 * @Date 2026/5/16 13:17
 * @Description 订单状态机
 */
public class OrderStateMachine extends BaseStateMachine<TradeOrderState, TradeOrderEvent> {


    public static final OrderStateMachine INSTANCE = new OrderStateMachine();

    {
        putTransition(TradeOrderState.CREATE, TradeOrderEvent.CONFIRM, TradeOrderState.CONFIRM);
        putTransition(TradeOrderState.CONFIRM, TradeOrderEvent.PAY, TradeOrderState.PAID);

        //库存预扣减成功，但是未真正扣减成功，也能支付/取消，不能因为延迟导致用户无法支付/取消。
        putTransition(TradeOrderState.CREATE, TradeOrderEvent.PAY, TradeOrderState.PAID);
        putTransition(TradeOrderState.CREATE, TradeOrderEvent.CANCEL, TradeOrderState.CLOSED);
        putTransition(TradeOrderState.CREATE, TradeOrderEvent.TIME_OUT, TradeOrderState.CLOSED);

        //订单创建过程中失败，推进到废弃态，这种状态用户看不到订单
        putTransition(TradeOrderState.CREATE, TradeOrderEvent.DISCARD, TradeOrderState.DISCARD);
        putTransition(TradeOrderState.CONFIRM, TradeOrderEvent.DISCARD, TradeOrderState.DISCARD);

        //已支付后，再确认，状态不变
        putTransition(TradeOrderState.PAID, TradeOrderEvent.CONFIRM, TradeOrderState.PAID);

        putTransition(TradeOrderState.CONFIRM, TradeOrderEvent.CANCEL, TradeOrderState.CLOSED);
        putTransition(TradeOrderState.CONFIRM, TradeOrderEvent.TIME_OUT, TradeOrderState.CLOSED);

        putTransition(TradeOrderState.PAID, TradeOrderEvent.FINISH, TradeOrderState.FINISH);
    }
}
