package com.beaker.mintcraft.collection.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Assert;
import com.beaker.mintcraft.api.chain.constant.ChainOperateBizType;
import com.beaker.mintcraft.api.chain.constant.ChainOperateType;
import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainOperationData;
import com.beaker.mintcraft.api.chain.service.ChainFacadeService;
import com.beaker.mintcraft.api.collection.request.CollectionPageQueryRequest;
import com.beaker.mintcraft.api.collection.request.HeldCollectionPageQueryRequest;
import com.beaker.mintcraft.api.collection.request.held.HeldCollectionDestroyRequest;
import com.beaker.mintcraft.api.collection.request.held.HeldCollectionTransferRequest;
import com.beaker.mintcraft.api.collection.service.CollectionFacadeService;
import com.beaker.mintcraft.api.collection.valobj.CollectionVO;
import com.beaker.mintcraft.api.collection.valobj.HeldCollectionVO;
import com.beaker.mintcraft.api.user.request.UserQueryRequest;
import com.beaker.mintcraft.api.user.response.UserQueryResponse;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.collection.domain.entity.HeldCollection;
import com.beaker.mintcraft.collection.domain.service.impl.HeldCollectionService;
import com.beaker.mintcraft.collection.infrastructure.exception.CollectionErrorCode;
import com.beaker.mintcraft.collection.infrastructure.exception.CollectionException;
import com.beaker.mintcraft.collection.param.DestroyParam;
import com.beaker.mintcraft.collection.param.TransferParam;
import com.beaker.mintcraft.web.util.MultiResultConvertor;
import com.beaker.mintcraft.web.vo.MultiResult;
import com.beaker.mintcraft.web.vo.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.beaker.mintcraft.api.common.constant.CommonConstant.SEPARATOR;
import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.TRANSFER_SELF_ERROR;
import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.USER_NOT_EXIST;
import static com.beaker.mintcraft.collection.infrastructure.exception.CollectionErrorCode.HELD_COLLECTION_OWNER_CHECK_ERROR;
import static com.beaker.mintcraft.collection.infrastructure.exception.CollectionErrorCode.HELD_COLLECTION_SAVE_FAILED;

/**
 * @Author beaker
 * @Date 2026/5/9 22:41
 * @Description 藏品接口
 */
@Slf4j
@RestController
@RequestMapping("/goods/collection")
public class CollectionController {

    @DubboReference
    private ChainFacadeService chainFacadeService;

    @DubboReference
    private UserFacadeService userFacadeService;

    @Resource
    private CollectionFacadeService collectionFacadeService;

    @Autowired
    private HeldCollectionService heldCollectionService;

    /**
     * 获取藏品信息
     *
     * @return
     */
    @GetMapping("/collectionInfo")
    public Result<CollectionVO> collectionInfo(@NotBlank String collectionId) {
        SingleResponse<CollectionVO> response = collectionFacadeService.queryById(Long.valueOf(collectionId));
        return Result.success(response.getData());
    }

    /**
     * 分页查询藏品信息
     *
     * @param state
     * @param keyword
     * @param pageSize
     * @param currentPage
     * @return
     */
    @GetMapping("/collectionList")
    public MultiResult<CollectionVO> collectionList(@NotBlank String state, String keyword, int pageSize, int currentPage) {
        CollectionPageQueryRequest collectionPageQueryRequest = new CollectionPageQueryRequest();
        collectionPageQueryRequest.setState(state);
        collectionPageQueryRequest.setKeyword(keyword);
        collectionPageQueryRequest.setCurrentPage(currentPage);
        collectionPageQueryRequest.setPageSize(pageSize);

        PageResponse<CollectionVO> pageResponse = collectionFacadeService.pageQuery(collectionPageQueryRequest);
        return MultiResultConvertor.convert(pageResponse);
    }

    /**
     * 用户持有藏品数量
     *
     * @return
     */
    @GetMapping("/heldCollectionCount")
    public Result<Long> heldCollectionCount() {
        String userId = (String) StpUtil.getLoginId();

        SingleResponse<Long> response = collectionFacadeService.queryHeldCollectionCount(userId);
        return Result.success(response.getData());
    }

    /**
     * 用户持有藏品信息
     *
     * @param heldCollectionId
     * @return
     */
    @GetMapping("/heldCollectionInfo")
    public Result<HeldCollectionVO> heldCollectionInfo(@NotBlank String heldCollectionId) {
        SingleResponse<HeldCollectionVO> response =
                collectionFacadeService.queryHeldCollectionById(Long.valueOf(heldCollectionId));

        return Result.success(response.getData());
    }

    @GetMapping("/heldCollectionList")
    public MultiResult<HeldCollectionVO> heldCollectionList(String keyword, String state, int pageSize, int currentPage) {
        String userId = (String) StpUtil.getLoginId();

        HeldCollectionPageQueryRequest request = new HeldCollectionPageQueryRequest();
        request.setUserId(userId);
        request.setState(state);
        request.setKeyword(keyword);
        request.setPageSize(pageSize);
        request.setCurrentPage(currentPage);

        PageResponse<HeldCollectionVO> response = collectionFacadeService.pageQueryHeldCollection(request);
        return MultiResultConvertor.convert(response);
    }

    @PostMapping("/destory")
    public Result<Boolean> destroy(@Valid @RequestBody DestroyParam param) {
        String userId = (String) StpUtil.getLoginId();

        HeldCollectionDestroyRequest request = new HeldCollectionDestroyRequest();
        request.setOperatorId(userId);
        request.setHeldCollectionId(param.getHeldCollectionId());
        request.setIdentifier(param.getHeldCollectionId());

        HeldCollection heldCollection = heldCollectionService.destroy(request);
        if (heldCollection != null) {
            ChainProcessRequest chainProcessRequest = new ChainProcessRequest();
            chainProcessRequest.setBizId(String.valueOf(param.getHeldCollectionId()));
            chainProcessRequest.setBizType(ChainOperateBizType.HELD_COLLECTION.name());
            chainProcessRequest.setIdentifier(param.getHeldCollectionId() + SEPARATOR + ChainOperateType.COLLECTION_DESTROY.name());
            chainProcessRequest.setClassId(String.valueOf(heldCollection.getCollectionId()));
            chainProcessRequest.setNtfId(heldCollection.getNftId());

            UserInfo owner = (UserInfo) StpUtil.getSession().get(userId);
            chainProcessRequest.setOwner(owner.getBlockChainUrl());

            ChainProcessResponse<ChainOperationData> response = chainFacadeService.destroy(chainProcessRequest);
            return Result.success(response.getSuccess());
        }

        return Result.success(false);
    }

    @PostMapping("/transfer")
    public Result<Boolean> transfer(@Valid @RequestBody TransferParam param) {
        String userId = (String) StpUtil.getLoginId();

        // 不能转移给自己
        if (userId.equals(param.getRecipientUserId())) {
            throw new CollectionException(TRANSFER_SELF_ERROR);
        }

        // 获取待转移藏品和接收用户
        SingleResponse<HeldCollectionVO> response = collectionFacadeService.queryHeldCollectionById(Long.parseLong(param.getHeldCollectionId()));
        HeldCollectionVO heldCollectionVO = response.getData();
        UserQueryRequest userQueryRequest = new UserQueryRequest(Long.valueOf(param.getRecipientUserId()));
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
        UserInfo recipient = userQueryResponse.getData();

        if (!userQueryResponse.getSuccess() || userQueryResponse.getData() == null) {
            throw new CollectionException(USER_NOT_EXIST);
        }

        if (heldCollectionVO != null) {
            // 藏品持有者与登录用户应当相同
            Assert.isTrue(StringUtils.equals(heldCollectionVO.getUserId(), userId), () -> new CollectionException(HELD_COLLECTION_OWNER_CHECK_ERROR));

            // 先变更数据库数据, 返回新藏品
            HeldCollectionTransferRequest transferRequest = new HeldCollectionTransferRequest();
            transferRequest.setRecipientUserId(param.getRecipientUserId());
            transferRequest.setHeldCollectionId(param.getHeldCollectionId());
            transferRequest.setOperatorId(userId);
            transferRequest.setIdentifier(param.getHeldCollectionId() + "_TRANSFER");

            HeldCollection transferHeldCollection = heldCollectionService.transfer(transferRequest);
            Assert.notNull(transferHeldCollection, () -> new CollectionException(HELD_COLLECTION_SAVE_FAILED));

            // 变更链上数据
            ChainProcessRequest request = new ChainProcessRequest();
            request.setBizId(String.valueOf(transferHeldCollection.getId()));
            request.setBizType(ChainOperateBizType.HELD_COLLECTION.name());
            request.setIdentifier(param.getHeldCollectionId() + SEPARATOR + param.getRecipientUserId() + SEPARATOR + ChainOperateType.COLLECTION_TRANSFER.name());
            request.setClassId(String.valueOf(heldCollectionVO.getCollectionId()));
            request.setNtfId(transferHeldCollection.getNftId());
            request.setRecipient(recipient.getBlockChainUrl());

            UserInfo owner = (UserInfo) StpUtil.getSession().get(userId);
            request.setOwner(owner.getBlockChainUrl());

            ChainProcessResponse<ChainOperationData> processResponse = chainFacadeService.transfer(request);

            return Result.success(processResponse.getSuccess());
        }

        return Result.success(false);
    }
}
