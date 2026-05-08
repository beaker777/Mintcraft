package com.beaker.mintcraft.notice.domain.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.api.notice.constant.NoticeState;
import com.beaker.mintcraft.api.notice.constant.NoticeType;
import com.beaker.mintcraft.base.exception.biz.BizException;
import com.beaker.mintcraft.notice.domain.entity.Notice;
import com.beaker.mintcraft.notice.infrastructure.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

import static com.beaker.mintcraft.base.exception.biz.BizErrorCode.NOTICE_SAVE_FAILED;

/**
 * @Author beaker
 * @Date 2026/5/8 14:32
 * @Description 通知服务
 */
@Service
public class NoticeService extends ServiceImpl<NoticeMapper, Notice> {

    private static final String SMS_NOTICE_TITLE = "验证码";

    public Notice saveCaptcha(String telephone, String captcha) {
        // 创建 notice 对象
        Notice notice = Notice.builder()
                .noticeTitle(SMS_NOTICE_TITLE)
                .noticeContent(captcha)
                .noticeType(NoticeType.SMS)
                .targetAddress(telephone)
                .state(NoticeState.INIT)
                .build();

        // 保存 notice
        Boolean saveResult = save(notice);

        if (!saveResult) {
            throw  new BizException(NOTICE_SAVE_FAILED);
        }

        return notice;
    }
}
