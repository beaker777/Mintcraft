package com.beaker.mintcraft.api.user.request;

import com.beaker.mintcraft.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/6/1 21:17
 * @Description 用户分页查询请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPageQueryRequest extends BaseRequest {

    /**
     * 手机号关键字
     */
    private String keyWord;

    /**
     * 用户状态
     */
    private String state;

    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 页面大小
     */
    private int pageSize;
}
