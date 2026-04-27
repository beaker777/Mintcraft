package com.beaker.mintcraft.base.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/4/26 21:06
 * @Description 返回多个数据的 Response
 */
@Getter
@Setter
public class MultiResponse<T> extends BaseResponse {

    public static final long serialVersionUID = 1L;

    private List<T> datas;

    public static <T> MultiResponse<T> of(List<T> datas) {
        MultiResponse<T> multiResponse = new MultiResponse<>();
        multiResponse.setSuccess(true);
        multiResponse.setDatas(datas);

        return multiResponse;
    }

    private static <T> MultiResponse<T> fail(String errorCode, String errorMessage) {
        MultiResponse<T> multiResponse = new MultiResponse<>();
        multiResponse.setSuccess(false);
        multiResponse.setResponseCode(errorCode);
        multiResponse.setResponseMessage(errorMessage);

        return multiResponse;
    }
}
