package com.beaker.mintcraft.chain.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.chain.domain.constant.ChainOperateState;
import com.beaker.mintcraft.chain.domain.entity.ChainOperateInfo;
import com.beaker.mintcraft.chain.infrastructure.mapper.ChainOperateInfoMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author beaker
 * @Date 2026/5/25 20:56
 * @Description 链操作信息服务
 */
@Service
public class ChainOperateInfoService extends ServiceImpl<ChainOperateInfoMapper, ChainOperateInfo> {

    public Long insertInfo(String chainType, String bizId, String bizType, String operateType, String param,String operationId) {
        ChainOperateInfo operateInfo = new ChainOperateInfo();
        operateInfo.setOperateTime(new Date());
        operateInfo.setChainType(chainType);
        operateInfo.setBizId(bizId);
        operateInfo.setBizType(bizType);
        operateInfo.setOperateType(operateType);
        operateInfo.setParam(param);
        operateInfo.setOutBizId(operationId);
        operateInfo.setState(ChainOperateState.PROCESSING);

        boolean saveResult = save(operateInfo);
        if (saveResult) {
            return operateInfo.getId();
        }
        return null;
    }

    public boolean updateResult(Long id, ChainOperateState state, String result) {
        ChainOperateInfo operateInfoDO = getById(id);
        operateInfoDO.setResult(result);
        operateInfoDO.setState(state);

        return updateById(operateInfoDO);
    }


    public ChainOperateInfo queryByOutBizId(String bizId, String bizType, String outBizId) {
        QueryWrapper<ChainOperateInfo> queryWrapper = new QueryWrapper<>();

        // 包装查询条件
        queryWrapper.eq("biz_id", bizId);
        queryWrapper.eq("biz_type", bizType);
        queryWrapper.eq("out_biz_id", outBizId);
        List<ChainOperateInfo> retList = list(queryWrapper);

        if (CollectionUtils.isEmpty(retList)) {
            return null;
        }
        return retList.getFirst();
    }
}
