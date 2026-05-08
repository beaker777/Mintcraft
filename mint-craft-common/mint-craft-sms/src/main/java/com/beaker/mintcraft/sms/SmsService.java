package com.beaker.mintcraft.sms;

import com.beaker.mintcraft.sms.response.SmsSendResponse;

/**
 * @Author beaker
 * @Date 2026/5/8 15:11
 * @Description 短信服务
 */
public interface SmsService {

    /**
     * 发送短信
     *
     * @param phoneNumber
     * @param code
     * @return
     */
    public SmsSendResponse sendMsg(String phoneNumber, String code);
}
