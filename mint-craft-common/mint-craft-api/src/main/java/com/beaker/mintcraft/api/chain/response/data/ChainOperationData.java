package com.beaker.mintcraft.api.chain.response.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author beaker
 * @Date 2026/5/25 20:32
 * @Description 链操作数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChainOperationData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作编号
     */
    private String operationId;
}
