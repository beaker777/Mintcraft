package com.beaker.mintcraft.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.beaker.mintcraft.admin.param.AdminCollectionCreateParam;
import com.beaker.mintcraft.api.collection.request.admin.CollectionCreateRequest;
import com.beaker.mintcraft.api.collection.response.CollectionChainResponse;
import com.beaker.mintcraft.api.collection.service.CollectionManageFacadeService;
import com.beaker.mintcraft.web.vo.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.beaker.mintcraft.api.common.constant.CommonConstant.COMMON_TIME_PATTERN;

/**
 * @Author beaker
 * @Date 2026/5/31 20:56
 * @Description 藏品管理 controller
 */
@Slf4j
@RestController
@RequestMapping("/admin/collection")
@CrossOrigin(origins = "*")
public class CollectionAdminController {

    @DubboReference
    private CollectionManageFacadeService collectionManageFacadeService;

    @PostMapping("/createCollection")
    public Result<Long> createCollection(@Valid @RequestBody AdminCollectionCreateParam param) throws Exception {
        String userId = (String) StpUtil.getLoginId();

        CollectionCreateRequest request = new CollectionCreateRequest();
        request.setIdentifier(UUID.randomUUID().toString());
        request.setPrice(param.getPrice());
        request.setQuantity(param.getQuantity());
        request.setName(param.getName());
        request.setDetail(param.getDetail());
        request.setCover(param.getCover());
        request.setCreatorId(userId);
        request.setCreateTime(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat(COMMON_TIME_PATTERN);
        request.setSaleTime(sdf.parse(param.getSaleTime()));

        if (param.getCanBook() == 1) {
            request.setBookStartTime(sdf.parse(param.getBookStartTime()));
            request.setBookEndTime(sdf.parse(param.getBookEndTime()));
        }
        request.setCanBook(param.getCanBook());

        // 创建藏品
        CollectionChainResponse response = collectionManageFacadeService.create(request);
        if (response.getSuccess()) {
            return Result.success(response.getCollectionId());
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }
}
