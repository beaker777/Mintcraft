package com.beaker.mintcraft.web.vo;

import lombok.Getter;
import lombok.Setter;

import static com.beaker.mintcraft.base.response.ResponseCode.SUCCESS;

/**
 * @Author beaker
 * @Date 2026/4/26 20:51
 * @Description 响应结果
 */
@Getter
@Setter
public class Result<T> {

    /**
     * 状态码
     */
    private String code;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 消息描述
     */
    private String message;

    /**
     * 数据，可以是任何类型的VO
     */
    private T data;

    public Result(){}

    public Result(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Result(String code, String message, Boolean success, T data) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS.name(), SUCCESS.name(), true, data);
    }

    public static <T> Result<T> error(String errorCode, String errorMessage) {
        return new Result<>(errorCode, errorMessage, false, null);
    }
}
