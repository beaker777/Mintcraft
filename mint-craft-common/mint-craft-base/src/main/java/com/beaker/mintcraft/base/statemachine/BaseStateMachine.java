package com.beaker.mintcraft.base.statemachine;

import com.beaker.mintcraft.base.exception.biz.BizException;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.beaker.mintcraft.base.exception.biz.BizErrorCode.STATE_MACHINE_TRANSITION_FAILED;

/**
 * @Author beaker
 * @Date 2026/5/16 13:11
 * @Description
 */
public class BaseStateMachine<STATE, EVENT> implements StateMachine<STATE, EVENT> {

    private Map<String, STATE> stateTransitions = Maps.newHashMap();

    protected void putTransition(STATE origin, EVENT event, STATE target) {
        // key: origin_event, value: target
        stateTransitions.put(Joiner.on("_").join(origin, event), target);
    }

    @Override
    public STATE transition(STATE state, EVENT event) {
        // 获取到转移后状态
        STATE target = stateTransitions.get(Joiner.on("_").join(state, event));
        if (target == null) {
            throw new BizException("state = " + state + ", event = " + event, STATE_MACHINE_TRANSITION_FAILED);
        }

        return target;
    }
}
