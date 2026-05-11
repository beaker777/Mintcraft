package com.beaker.mintcraft.collection.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.beaker.mintcraft.api.collection.request.CollectionPageQueryRequest;
import com.beaker.mintcraft.api.collection.service.CollectionFacadeService;
import com.beaker.mintcraft.api.collection.valobj.CollectionVO;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.web.util.MultiResultConvertor;
import com.beaker.mintcraft.web.vo.MultiResult;
import com.beaker.mintcraft.web.vo.Result;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author beaker
 * @Date 2026/5/9 22:41
 * @Description 藏品接口
 */
@Slf4j
@RestController
@RequestMapping("/goods/collection")
public class CollectionController {

    @Resource
    private CollectionFacadeService collectionFacadeService;

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
}
