package com.beaker.mintcraft.base.response;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author beaker
 * @Date 2026/4/26 21:00
 * @Description 返回单个数据的 Response
 */
@Getter
@Setter
public class SingleResponse<T> extends BaseResponse {

    public static final long serialVersionUID = 1L;

    private T data;

    public static <T> SingleResponse<T> of(T data) {
        SingleResponse<T> singleResponse = new SingleResponse<>();
        singleResponse.setSuccess(true);
        singleResponse.setData(data);

        return singleResponse;
    }

    private static <T> SingleResponse<T> fail(String errorCode, String errorMessage) {
        SingleResponse<T> singleResponse = new SingleResponse<>();
        singleResponse.setSuccess(false);
        singleResponse.setResponseCode(errorCode);
        singleResponse.setResponseMessage(errorMessage);

        return singleResponse;
    }
}
