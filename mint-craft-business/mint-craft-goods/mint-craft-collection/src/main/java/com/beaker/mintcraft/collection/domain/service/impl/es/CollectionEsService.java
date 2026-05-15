package com.beaker.mintcraft.collection.domain.service.impl.es;

import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.collection.domain.entity.Collection;
import com.beaker.mintcraft.collection.domain.service.impl.CollectionServiceImpl;
import com.beaker.mintcraft.collection.infrastructure.mapper.es.CollectionEsMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @Author beaker
 * @Date 2026/5/12 18:53
 * @Description ES 优化分页查询
 */
@Service
@ConditionalOnProperty(value = "spring.elasticsearch.enable", havingValue = "true")
public class CollectionEsService extends CollectionServiceImpl {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Resource
    private CollectionEsMapper collectionEsMapper;

    @Override
    public PageResponse<Collection> pageQueryByState(String keyWord, String state, int currentPage, int pageSize) {
        // 包装查询条件
        Criteria criteria = null;
        if (StringUtils.isNotBlank(keyWord)) {
            criteria = new Criteria("name").is(keyWord).
                    and(new Criteria("state").is(state), new Criteria("deleted").is("0"));
        } else if (StringUtils.isNotBlank(state)) {
            criteria = new Criteria("state").is(state).and(new Criteria("deleted").is("0"));
        } else {
            criteria = new Criteria("deleted").is("0");
        }

        // 进行查询
        PageRequest pageRequest = PageRequest.of(currentPage - 1, pageSize);
        Query query = new CriteriaQuery(criteria).
                setPageable(pageRequest).addSort(Sort.by(Sort.Order.desc("create_time")));
        SearchHits<Collection> searchHits = elasticsearchOperations.search(query, Collection.class);

        return PageResponse.of(searchHits.getSearchHits().stream().map(SearchHit::getContent).toList(),
                (int) searchHits.getTotalHits(), pageSize, currentPage);
    }
}
