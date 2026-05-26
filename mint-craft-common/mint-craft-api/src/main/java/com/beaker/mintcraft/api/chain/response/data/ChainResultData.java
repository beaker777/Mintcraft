package com.beaker.mintcraft.api.chain.response.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author beaker
 * @Date 2026/5/26 17:24
 * @Description 链结果数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChainResultData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ntf唯一编号
     */
    private String nftId;

    /**
     * 交易哈希
     */
    private String txHash;

    /**
     * 状态
     */
    private String state;

    /**
     * '藏品编号'
     */
    private String serialNo;

}
