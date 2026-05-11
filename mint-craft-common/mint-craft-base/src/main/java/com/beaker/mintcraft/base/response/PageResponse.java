package com.beaker.mintcraft.base.response;

import lombok.Data;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/5/11 17:28
 * @Description 分页查询响应类
 */
@Data
public class PageResponse <T> extends MultiResponse<T> {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 每页结果数
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 总数
     */
    private int total;

    public static <T> PageResponse<T> of(List<T> datas, int total, int pageSize, int currentPage) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setSuccess(true);
        pageResponse.setDatas(datas);
        pageResponse.setTotal(total);
        pageResponse.setPageSize(pageSize);
        pageResponse.setCurrentPage(currentPage);
        pageResponse.setTotalPage((pageSize + total - 1) / pageSize);
        return pageResponse;
    }
}
