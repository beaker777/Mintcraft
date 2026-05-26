package com.beaker.mintcraft.chain.job;

import com.beaker.mintcraft.api.chain.constant.ChainType;
import com.beaker.mintcraft.api.chain.request.ChainQueryRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainResultData;
import com.beaker.mintcraft.base.exception.biz.RepoErrorCode;
import com.beaker.mintcraft.chain.domain.constant.ChainOperateState;
import com.beaker.mintcraft.chain.domain.entity.ChainOperateInfo;
import com.beaker.mintcraft.chain.domain.service.ChainOperateInfoService;
import com.beaker.mintcraft.chain.domain.service.ChainService;
import com.beaker.mintcraft.chain.domain.service.factory.ChainServiceFactory;
import com.beaker.mintcraft.chain.infrastructure.exception.ChainErrorCode;
import com.beaker.mintcraft.chain.infrastructure.exception.ChainException;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/5/26 19:35
 * @Description 链处理任务
 */
@Component
public class ChainProcessJob {

    @Autowired
    private ChainOperateInfoService chainOperateInfoService;

    @Autowired
    private ChainServiceFactory chainServiceFactory;

    private static final int PAGE_SIZE = 5;

    private static final Logger LOG = LoggerFactory.getLogger(ChainProcessJob.class);

    @XxlJob("unFinishOperateExecute")
    public ReturnT<String> execute() {
        Long minId = chainOperateInfoService.queryMinIdByState(ChainOperateState.PROCESSING.name());

        List<ChainOperateInfo> chainOperateInfos = chainOperateInfoService
                .pageQueryOperateInfoByState(ChainOperateState.PROCESSING.name(), PAGE_SIZE, minId);

        while (CollectionUtils.isNotEmpty(chainOperateInfos)) {
            chainOperateInfos.forEach(this::executeSingle);

            // 根据本次查询的最大 id 继续扫描
            long maxId = chainOperateInfos.stream().mapToLong(ChainOperateInfo::getId).max().orElse(Long.MAX_VALUE);
            chainOperateInfos = chainOperateInfoService
                    .pageQueryOperateInfoByState(ChainOperateState.PROCESSING.name(), PAGE_SIZE, maxId + 1);
        }

        return ReturnT.SUCCESS;
    }

    private void executeSingle(ChainOperateInfo chainOperateInfo) {
        LOG.info("start to execute unfinish operate , id is {}", chainOperateInfo.getId());

        try {
            // 根据链类型获取 service
            ChainService chainService = chainServiceFactory.get(ChainType.valueOf(chainOperateInfo.getChainType()));

            // 查询 OperateData 这里直接 mock 了一个, 正常要去第三方服务查一遍
            ChainQueryRequest chainQueryRequest = new ChainQueryRequest();
            chainQueryRequest.setOperationId(chainOperateInfo.getOutBizId());
            ChainProcessResponse<ChainResultData> chainProcessResponse = chainService.queryChainResult(chainQueryRequest);

            if (!chainProcessResponse.getSuccess()) {
                throw new ChainException(ChainErrorCode.CHAIN_QUERY_FAIL);
            }

            ChainResultData chainResultData = chainProcessResponse.getData();
            // 判断异常情况
            if (chainResultData == null) {
                throw new ChainException(ChainErrorCode.CHAIN_QUERY_FAIL);
            }
            if (!StringUtils.equals(chainResultData.getState(), ChainOperateState.SUCCEED.name())) {
                throw new ChainException(ChainErrorCode.CHAIN_PROCESS_STATE_ERROR);
            }

            // 链操作成功处理, 发送消息
            chainService.sendMsg(chainOperateInfo, chainResultData);
            // 更新 chainOperateInfo
            boolean updateResult = chainOperateInfoService
                    .updateResult(chainOperateInfo.getId(), ChainOperateState.SUCCEED, null);
            if (!updateResult) {
                throw new ChainException(RepoErrorCode.UPDATE_FAILED);
            }
        } catch (Exception e) {
            LOG.error("start to execute unfinish operate error, id is {}, error is {}", chainOperateInfo.getId(), e);
        }
    }
}
