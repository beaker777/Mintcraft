package com.beaker.mintcraft.sms.impl;

import com.beaker.mintcraft.lock.DistributeLock;
import com.beaker.mintcraft.sms.SmsService;
import com.beaker.mintcraft.sms.response.SmsSendResponse;

/**
 * @Author beaker
 * @Date 2026/5/8 18:13
 * @Description mock 信息发送实现类
 */
public class MockSmsServiceImpl implements SmsService {

    @DistributeLock(scene = "SEND_SMS", keyExpression = "#phoneNumber")
    @Override
    public SmsSendResponse sendMsg(String phoneNumber, String code) {
        SmsSendResponse smsSendResponse = new SmsSendResponse();
        smsSendResponse.setSuccess(true);
        return smsSendResponse;
    }
}
