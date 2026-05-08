package com.beaker.mintcraft.notice.facade;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.beaker.mintcraft.api.notice.constant.NoticeState;
import com.beaker.mintcraft.api.notice.response.NoticeResponse;
import com.beaker.mintcraft.api.notice.service.NoticeFacadeService;
import com.beaker.mintcraft.base.exception.system.SystemException;
import com.beaker.mintcraft.limiter.RateLimiter;
import com.beaker.mintcraft.notice.domain.entity.Notice;
import com.beaker.mintcraft.notice.domain.service.NoticeService;
import com.beaker.mintcraft.rpc.facade.Facade;
import com.beaker.mintcraft.sms.SmsService;
import com.beaker.mintcraft.sms.response.SmsSendResponse;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.beaker.mintcraft.api.notice.constant.NoticeConstant.CAPTCHA_KEY_PREFIX;
import static com.beaker.mintcraft.base.exception.biz.BizErrorCode.SEND_NOTICE_DUPLICATED;

/**
 * @Author beaker
 * @Date 2026/5/8 14:52
 * @Description notice 模块 facade 实现类
 */
@DubboService
public class NoticeFacadeServiceImpl implements NoticeFacadeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private NoticeService noticeService;

    @Resource
    private SmsService smsService;

    @Resource
    private RateLimiter rateLimiter;

    @Facade
    @Override
    public NoticeResponse generateAndSendSmsCaptcha(String telephone) {
        // 限流, 每个电话号一分钟只能发送一条验证码
        Boolean access = rateLimiter.tryAcquire(telephone, 1, 60);
        if (!access) {
            throw new SystemException(SEND_NOTICE_DUPLICATED);
        }

        // 生成验证码
        String captcha = RandomUtil.randomNumbers(4);

        // 验证码存入 Redis, 5 min 后过期
        stringRedisTemplate.opsForValue().set(CAPTCHA_KEY_PREFIX + telephone, captcha, 5, TimeUnit.MINUTES);

        // 将 notice 记录存入数据库
        Notice notice = noticeService.saveCaptcha(telephone, captcha);

        Thread.ofVirtual().start(() -> {
            // fixme: 在 dev 环境下我们使用的是 mock 的短信发送
            SmsSendResponse result = smsService.sendMsg(notice.getTargetAddress(), notice.getNoticeContent());
            if (result.getSuccess()) {
                // 如果信息发送成功, 更新 notice 状态
                notice.setState(NoticeState.SUCCESS);
                notice.setSendSuccessTime(new Date());
                noticeService.updateById(notice);
            } else {
                // 如果信息发送失败, 更新 notice 状态和备注
                notice.setState(NoticeState.FAILED);
                notice.addExtendInfo("executeInfo", JSON.toJSONString(result));
                noticeService.updateById(notice);
            }
        });

        NoticeResponse noticeResponse = new NoticeResponse();
        noticeResponse.setSuccess(true);
        return noticeResponse;
    }
}
