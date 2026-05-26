package com.beaker.mintcraft.api.chain.model;

import com.beaker.mintcraft.api.chain.constant.ChainOperateBizType;
import com.beaker.mintcraft.api.chain.constant.ChainOperateType;
import com.beaker.mintcraft.api.chain.constant.ChainType;
import com.beaker.mintcraft.api.chain.response.data.ChainResultData;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/26 17:41
 * @Description 链操作体
 */
@Data
public class ChainOperateBody {

    /**
     * 业务id
     */
    private String bizId;

    /**
     * 业务类型
     */
    private ChainOperateBizType bizType;

    /**
     * 操作类型
     */
    private ChainOperateType operateType;

    /**
     * 操作信息id
     */
    private Long operateInfoId;

    /**
     * 链类型
     */
    private ChainType chainType;

    /**
     * 具体业务数据
     */
    private ChainResultData chainResultData;
}
