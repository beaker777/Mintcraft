package com.beaker.mintcraft.api.chain.request;

import com.beaker.mintcraft.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/5/26 17:29
 * @Description 链查询请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChainQueryRequest extends BaseRequest {


    /**
     * 操作id
     */
    private String operationId;

    /**
     * 操作信息的主键 ID
     */
    private String operationInfoId;
}
