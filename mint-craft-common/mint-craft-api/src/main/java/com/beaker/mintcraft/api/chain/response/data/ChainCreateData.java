package com.beaker.mintcraft.api.chain.response.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author beaker
 * @Date 2026/5/24 17:51
 * @Description 链创建
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChainCreateData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作编号
     */
    private String operationId;

    /**
     * 链账户地址
     */
    private String account;

    /**
     * 链账户名称
     */
    private String name;

    /**
     * 平台名称
     */
    private String platform;
}
