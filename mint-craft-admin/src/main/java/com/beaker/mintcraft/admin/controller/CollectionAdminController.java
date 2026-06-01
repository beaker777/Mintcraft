package com.beaker.mintcraft.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.beaker.mintcraft.admin.infrastructure.exception.AdminException;
import com.beaker.mintcraft.admin.param.AdminCollectionCreateParam;
import com.beaker.mintcraft.admin.param.AdminCollectionModifyParam;
import com.beaker.mintcraft.admin.param.AdminCollectionRemoveParam;
import com.beaker.mintcraft.api.collection.request.CollectionPageQueryRequest;
import com.beaker.mintcraft.api.collection.request.admin.CollectionCreateRequest;
import com.beaker.mintcraft.api.collection.request.admin.CollectionModifyInventoryRequest;
import com.beaker.mintcraft.api.collection.request.admin.CollectionModifyPriceRequest;
import com.beaker.mintcraft.api.collection.request.admin.CollectionRemoveRequest;
import com.beaker.mintcraft.api.collection.response.CollectionChainResponse;
import com.beaker.mintcraft.api.collection.response.CollectionModifyResponse;
import com.beaker.mintcraft.api.collection.response.CollectionRemoveResponse;
import com.beaker.mintcraft.api.collection.service.CollectionFacadeService;
import com.beaker.mintcraft.api.collection.service.CollectionManageFacadeService;
import com.beaker.mintcraft.api.collection.valobj.CollectionVO;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.file.FileService;
import com.beaker.mintcraft.web.util.MultiResultConvertor;
import com.beaker.mintcraft.web.vo.MultiResult;
import com.beaker.mintcraft.web.vo.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.beaker.mintcraft.admin.infrastructure.exception.AdminErrorCode.ADMIN_UPLOAD_PICTURE_FAIL;
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

    @DubboReference
    private CollectionFacadeService collectionFacadeService;

    @Autowired
    private FileService fileService;


    @PostMapping("/createCollection")
    public Result<Long> createCollection(@Valid @RequestBody AdminCollectionCreateParam param) throws Exception {
        String userId = (String) StpUtil.getLoginId();

        // 构造请求
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

    @PostMapping("/uploadCollection")
    public Result<String> uploadCollection(@RequestParam("file_data")MultipartFile file) throws Exception {
        if (file == null) {
            throw new AdminException(ADMIN_UPLOAD_PICTURE_FAIL);
        }

        String userId = (String) StpUtil.getLoginId();

        // 上传藏品封面
        String prefix = "https://mintcraft.oss-cn-beijing.aliyuncs.com/";
        String filename = file.getOriginalFilename();
        InputStream fileStream = file.getInputStream();
        String path = "collection/" + userId + "/" + filename;
        boolean res = fileService.upload(path, fileStream);

        if (!res) {
            throw new AdminException(ADMIN_UPLOAD_PICTURE_FAIL);
        }

        return Result.success(prefix + path);
    }

    @PostMapping("/modifyInventory")
    public Result<Long> modifyInventory(@Valid @RequestBody AdminCollectionModifyParam param) {
        CollectionModifyInventoryRequest request = new CollectionModifyInventoryRequest();
        request.setIdentifier(UUID.randomUUID().toString());
        request.setCollectionId(param.getCollectionId());
        request.setQuantity(param.getQuantity());

        CollectionModifyResponse response = collectionManageFacadeService.modifyInventory(request);
        if (response.getSuccess()) {
            return Result.success(response.getCollectionId());
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    @PostMapping("/modifyPrice")
    public Result<Long> modifyPrice(@Valid @RequestBody AdminCollectionModifyParam param) {
        CollectionModifyPriceRequest request = new CollectionModifyPriceRequest();
        request.setIdentifier(UUID.randomUUID().toString());
        request.setCollectionId(param.getCollectionId());
        request.setPrice(param.getPrice());

        CollectionModifyResponse response = collectionManageFacadeService.modifyPrice(request);
        if (response.getSuccess()) {
            return Result.success(response.getCollectionId());
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    @PostMapping("/removeCollection")
    public Result<Long> removeCollection(@Valid @RequestBody AdminCollectionRemoveParam param) {
        CollectionRemoveRequest request = new CollectionRemoveRequest();
        request.setIdentifier(UUID.randomUUID().toString());
        request.setCollectionId(param.getCollectionId());

        CollectionRemoveResponse response = collectionManageFacadeService.remove(request);
        if (response.getSuccess()) {
            return Result.success(response.getCollectionId());
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    @GetMapping("/collectionList")
    public MultiResult<CollectionVO> collectionList(String state, String keyWord, int pageSize, int currentPage) {
        CollectionPageQueryRequest collectionPageQueryRequest = new CollectionPageQueryRequest();
        collectionPageQueryRequest.setState(state);
        collectionPageQueryRequest.setKeyword(keyWord);
        collectionPageQueryRequest.setPageSize(pageSize);
        collectionPageQueryRequest.setCurrentPage(currentPage);

        PageResponse<CollectionVO> pageResponse = collectionFacadeService.pageQuery(collectionPageQueryRequest);
        return MultiResultConvertor.convert(pageResponse);
    }
}
