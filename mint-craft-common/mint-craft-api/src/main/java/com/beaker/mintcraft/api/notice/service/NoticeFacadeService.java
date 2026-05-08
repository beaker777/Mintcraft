package com.beaker.mintcraft.api.notice.service;

import com.beaker.mintcraft.api.notice.response.NoticeResponse;

/**
 * @Author beaker
 * @Date 2026/5/8 14:51
 * @Description notice 模块 facade 层
 */
public interface NoticeFacadeService {

    /**
     * 生成并发送短信验证码
     *
     * @param telephone
     * @return
     */
    public NoticeResponse generateAndSendSmsCaptcha(String telephone);
}

