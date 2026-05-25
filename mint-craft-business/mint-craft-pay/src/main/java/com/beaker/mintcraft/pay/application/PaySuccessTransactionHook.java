package com.beaker.mintcraft.pay.application;

import com.beaker.mintcraft.api.chain.constant.ChainOperateBizType;
import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.service.ChainFacadeService;
import com.beaker.mintcraft.api.collection.service.CollectionFacadeService;
import com.beaker.mintcraft.api.collection.valobj.HeldCollectionVO;
import com.beaker.mintcraft.api.user.request.UserQueryRequest;
import com.beaker.mintcraft.api.user.response.UserQueryResponse;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.base.utils.SpringContextHolder;
import com.beaker.mintcraft.rpc.support.RemoteCallWrapper;
import io.seata.tm.api.transaction.TransactionHook;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author beaker
 * @Date 2026/5/25 20:15
 * @Description 支付成功事务回调
 */
@Slf4j
public class PaySuccessTransactionHook implements TransactionHook {

    /**
     * 从 Spring 的上下文中获取到 Bean
     */
    CollectionFacadeService collectionFacadeService = (CollectionFacadeService) SpringContextHolder.getBean("collectionFacadeService");

    UserFacadeService userFacadeService = (UserFacadeService) SpringContextHolder.getBean("userFacadeService");

    ChainFacadeService chainFacadeService = (ChainFacadeService) SpringContextHolder.getBean("chainFacadeService");

    private Long heldCollectionId;

    public PaySuccessTransactionHook() {
    }

    public PaySuccessTransactionHook(Long heldCollectionId) {
        this.heldCollectionId = heldCollectionId;
    }

    @Override
    public void beforeBegin() {
        //do nothing
    }

    @Override
    public void afterBegin() {
        //do nothing
    }

    @Override
    public void beforeCommit() {
        //do nothing
    }

    @Override
    public void afterCommit() {
        log.info("transaction is commit ,start to mint , heldCollectionId : " + heldCollectionId);

        SingleResponse<HeldCollectionVO> response = collectionFacadeService.queryHeldCollectionById(heldCollectionId);

        if (response.getSuccess()) {
            // 获取到待上链的藏品
            HeldCollectionVO heldCollection = response.getData();

            // 获取到持有藏品的用户
            UserQueryRequest userQueryRequest = new UserQueryRequest(Long.valueOf(heldCollection.getUserId()));
            UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);

            ChainProcessRequest chainProcessRequest = new ChainProcessRequest();
            chainProcessRequest.setRecipient(userQueryResponse.getData().getBlockChainUrl());
            chainProcessRequest.setClassId(heldCollection.getCollectionId().toString());
            chainProcessRequest.setClassName(heldCollection.getName());
            chainProcessRequest.setSerialNo(heldCollection.getSerialNo());
            chainProcessRequest.setBizId(heldCollection.getId());
            chainProcessRequest.setBizType(ChainOperateBizType.HELD_COLLECTION.name());
            chainProcessRequest.setIdentifier(heldCollection.getId());

            // rpc 调用藏品上链
            // TODO: 如果失败了, 则依靠定时任务补偿
            RemoteCallWrapper.call(req -> chainFacadeService.mint(req), chainProcessRequest, "mint");
            log.info("transaction is commit ,end to mint , heldCollectionId : " + heldCollectionId);
        }
    }

    @Override
    public void beforeRollback() {
        //do nothing
    }

    @Override
    public void afterRollback() {
        log.info("transaction is rollback, do nothing : " + heldCollectionId);
    }

    @Override
    public void afterCompletion() {
        //do nothing
    }
}
