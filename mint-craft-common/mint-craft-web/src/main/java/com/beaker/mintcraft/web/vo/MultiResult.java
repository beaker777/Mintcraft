package com.beaker.mintcraft.web.vo;

import lombok.Data;

import java.util.List;

import static com.beaker.mintcraft.base.response.ResponseCode.SUCCESS;

/**
 * @Author beaker
 * @Date 2026/5/11 17:11
 * @Description 多个结果查询
 */
@Data
public class MultiResult <T> extends Result<List<T>> {

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private int page;

    /**
     * 每页记录数
     */
    private int size;

    public MultiResult() {
        super();
    }

    public MultiResult(String code, String message, boolean success, List<T> data, long total, int page, int size) {
        super(code, message, success, data);
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public static <T> MultiResult<T> successMulti(List<T> data, long total, int page, int size) {
        return new MultiResult<>(SUCCESS.name(), SUCCESS.name(), true, data, total, page, size);
    }

    public static <T> MultiResult<T> errorMulti(String errorCode, String errorMsg) {
        return new MultiResult<>(errorCode, errorMsg, false, null, 0, 0, 0);
    }

}
