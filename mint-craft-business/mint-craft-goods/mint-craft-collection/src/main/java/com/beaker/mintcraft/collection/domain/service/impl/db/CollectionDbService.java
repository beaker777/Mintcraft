package com.beaker.mintcraft.collection.domain.service.impl.db;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.collection.domain.entity.Collection;
import com.beaker.mintcraft.collection.domain.service.impl.CollectionServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @Author beaker
 * @Date 2026/5/11 17:38
 * @Description MySQL 实现分页查询
 */
@Service
@ConditionalOnProperty(value = "spring.elasticsearch.enable", havingValue = "false")
public class CollectionDbService extends CollectionServiceImpl {

    @Override
    public PageResponse<Collection> pageQueryByState(String keyWord, String state, int currentPage, int pageSize) {
        Page<Collection> page = new Page<>(currentPage, pageSize);
        QueryWrapper<Collection> wrapper = new QueryWrapper<>();

        // 包装查询条件
        if (StringUtils.isNotBlank(state)) {
            wrapper.eq("state", state);
        }
        if (StringUtils.isNotBlank(keyWord)) {
            wrapper.like("name", keyWord);
        }

        // 分页查询
        Page<Collection> collectionPage = this.page(page, wrapper);

        return PageResponse.of(collectionPage.getRecords(), (int) collectionPage.getTotal(), pageSize, currentPage);
    }
}
