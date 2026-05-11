package com.beaker.mintcraft.web.util;

import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.web.vo.MultiResult;

import static com.beaker.mintcraft.base.response.ResponseCode.SUCCESS;

/**
 * @Author beaker
 * @Date 2026/5/11 17:54
 * @Description 多条记录结果转换器
 */
public class MultiResultConvertor {

    public static <T> MultiResult<T> convert(PageResponse<T> pageResponse) {
        MultiResult<T> multiResult = new MultiResult<T>(
                SUCCESS.name(), SUCCESS.name(), true, pageResponse.getDatas(),
                pageResponse.getTotal(), pageResponse.getCurrentPage(), pageResponse.getPageSize());

        return multiResult;
    }
}
