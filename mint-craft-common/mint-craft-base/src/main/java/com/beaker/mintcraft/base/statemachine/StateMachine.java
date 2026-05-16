package com.beaker.mintcraft.base.statemachine;

/**
 * @Author beaker
 * @Date 2026/5/16 13:08
 * @Description 状态机接口
 */
public interface StateMachine<STATE, EVENT> {

    /**
     * 状态机状态转移
     *
     * @param state
     * @param event
     * @return
     */
    public STATE transition(STATE state, EVENT event);
}
