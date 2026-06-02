package com.beaker.mintcraft.collection.job;

import com.beaker.mintcraft.api.chain.constant.ChainOperateBizType;
import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.service.ChainFacadeService;
import com.beaker.mintcraft.api.user.request.UserQueryRequest;
import com.beaker.mintcraft.api.user.response.UserQueryResponse;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import com.beaker.mintcraft.collection.domain.entity.HeldCollection;
import com.beaker.mintcraft.collection.domain.service.impl.HeldCollectionService;
import com.beaker.mintcraft.rpc.support.RemoteCallWrapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/6/2 20:04
 * @Description 藏品上链重试
 */
@Component
public class CollectionChainMintRetryJob {

    @Autowired
    private HeldCollectionService heldCollectionService;

    @DubboReference
    private UserFacadeService userFacadeService;

    @DubboReference
    private ChainFacadeService chainFacadeService;

    private static final int PAGE_SIZE = 100;

    private static final Logger LOG = LoggerFactory.getLogger(CollectionChainMintRetryJob.class);

    @XxlJob("collectionChainMintRetryJob")
    public ReturnT<String> execute() {
        Long minId = heldCollectionService.queryMinIdForMint();

        List<HeldCollection> heldCollections = heldCollectionService.pageQueryForChainMint(PAGE_SIZE, minId);

        while (CollectionUtils.isNotEmpty(heldCollections)) {
            heldCollections.forEach(this::executeSingle);

            Long maxId = heldCollections.stream().mapToLong(HeldCollection::getId).max().orElse(Integer.MAX_VALUE);
            heldCollections = heldCollectionService.pageQueryForChainMint(PAGE_SIZE, maxId + 1);
        }

        return ReturnT.SUCCESS;
    }

    private void executeSingle(HeldCollection heldCollection) {
        LOG.info("start to execute chainMint retry , heldCollectionId is {}", heldCollection.getId());

        UserQueryRequest userQueryRequest = new UserQueryRequest(Long.valueOf(heldCollection.getUserId()));
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);

        ChainProcessRequest chainProcessRequest = new ChainProcessRequest();
        chainProcessRequest.setRecipient(userQueryResponse.getData().getBlockChainUrl());
        chainProcessRequest.setClassId(heldCollection.getCollectionId().toString());
        chainProcessRequest.setClassName(heldCollection.getName());
        chainProcessRequest.setSerialNo(heldCollection.getSerialNo());
        chainProcessRequest.setBizId(heldCollection.getId().toString());
        chainProcessRequest.setBizType(ChainOperateBizType.HELD_COLLECTION.name());
        chainProcessRequest.setIdentifier(heldCollection.getId().toString());

        // 失败后, 依靠定时任务补偿
        RemoteCallWrapper.call(req -> chainFacadeService.mint(req), chainProcessRequest, "mint");
        LOG.info("transaction is commit, end to mint, heldCollectionId: " + heldCollection.getId());
    }
}
