package com.beaker.mintcraft.base.response;

import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/4/26 21:00
 * @Description 返回单个数据的 Response
 */
@Data
public class SingleResponse<T> extends BaseResponse {

    public static final long serialVersionUID = 1L;

    private T data;

    public static <T> SingleResponse<T> of(T data) {
        SingleResponse<T> singleResponse = new SingleResponse<>();
        singleResponse.setSuccess(true);
        singleResponse.setData(data);

        return singleResponse;
    }

    public static <T> SingleResponse<T> fail(String errorCode, String errorMessage) {
        SingleResponse<T> singleResponse = new SingleResponse<>();
        singleResponse.setSuccess(false);
        singleResponse.setResponseCode(errorCode);
        singleResponse.setResponseMessage(errorMessage);

        return singleResponse;
    }
}
