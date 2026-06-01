package com.beaker.mintcraft.admin.infrastructure.exception;

import com.beaker.mintcraft.base.exception.ErrorCode;

/**
 * @Author beaker
 * @Date 2026/6/1 18:19
 * @Description admin 错误码
 */
public enum AdminErrorCode implements ErrorCode {

    /**
     * 后台上传图片失败
     */
    ADMIN_UPLOAD_PICTURE_FAIL("ADMIN_UPLOAD_PICTURE_FAIL", "后台上传图片失败"),

    ADMIN_USER_NOT_EXIST("ADMIN_USER_NOT_EXIST", "后台用户不存在"),

    ADMIN_USER_PASSWORD_ERROR("ADMIN_USER_PASSWORD_ERROR", "后台用户密码错误"),

    USER_NOT_LOGIN("USER_NOT_LOGIN", "后台用户未登录");


    private String code;

    private String message;

    AdminErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
