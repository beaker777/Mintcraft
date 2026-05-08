package com.beaker.mintcraft.sms.impl;

import com.beaker.mintcraft.base.utils.RestClientUtils;
import com.beaker.mintcraft.lock.DistributeLock;
import com.beaker.mintcraft.sms.SmsService;
import com.beaker.mintcraft.sms.response.SmsSendResponse;
import com.google.common.collect.Maps;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static com.beaker.mintcraft.base.response.ResponseCode.SYSTEM_ERROR;

/**
 * @Author beaker
 * @Date 2026/5/8 15:12
 * @Description 短信服务实现类
 */
@Slf4j
@Setter
public class SmsServiceImpl implements SmsService {

    private static Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    private String host;

    private String path;

    private String appcode;

    private String smsSignId;

    private String templateId;

    @DistributeLock(scene = "SEND_SMS", keyExpression = "#phoneNumber")
    @Override
    public SmsSendResponse sendMsg(String phoneNumber, String code) {
        SmsSendResponse smsSendResponse = new SmsSendResponse();

        String method = "POST";

        // header
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(1);
        headers.put("Authorization", "APPCODE " + appcode);

        // query
        Map<String, String> queries = Maps.newHashMapWithExpectedSize(4);
        queries.put("mobile", phoneNumber);
        queries.put("param", "**code**:" + code + "**minute**:5");
        queries.put("smsSignId", smsSignId);
        queries.put("templateId", templateId);

        // body
        Map<String, String> bodies = Maps.newHashMapWithExpectedSize(2);

        // 发送请求
        try {
            ResponseEntity response = RestClientUtils.doPost(host, path, headers, queries, bodies);
            if (response.getStatusCode().is2xxSuccessful()) {
                smsSendResponse.setSuccess(true);
            }
        } catch (Exception e) {
            logger.error("sendMsg error", e);
            smsSendResponse.setSuccess(false);
            smsSendResponse.setResponseCode(SYSTEM_ERROR.name());
            smsSendResponse.setResponseMessage(StringUtils.substring(e.toString(), 0, 1000));
        }

        return smsSendResponse;
    }
}
