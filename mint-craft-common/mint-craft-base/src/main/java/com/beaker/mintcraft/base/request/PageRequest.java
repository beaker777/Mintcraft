package com.beaker.mintcraft.base.request;

import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/11 17:21
 * @Description 分页查询
 */
@Data
public class PageRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 每页结果数
     */
    private int pageSize;
}
