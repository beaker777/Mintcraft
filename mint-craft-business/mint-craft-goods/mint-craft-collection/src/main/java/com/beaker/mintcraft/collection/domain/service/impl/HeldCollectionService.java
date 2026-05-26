package com.beaker.mintcraft.collection.domain.service.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson2.JSON;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.api.collection.constant.GoodsSaleBizType;
import com.beaker.mintcraft.api.collection.constant.HeldCollectionEventType;
import com.beaker.mintcraft.api.collection.constant.HeldCollectionState;
import com.beaker.mintcraft.api.collection.model.HeldCollectionDTO;
import com.beaker.mintcraft.api.collection.request.HeldCollectionPageQueryRequest;
import com.beaker.mintcraft.api.collection.request.held.HeldCollectionActiveRequest;
import com.beaker.mintcraft.api.collection.request.held.HeldCollectionCreateRequest;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.cache.constant.CacheConstant;
import com.beaker.mintcraft.collection.domain.entity.HeldCollection;
import com.beaker.mintcraft.collection.domain.entity.HeldCollectionStream;
import com.beaker.mintcraft.collection.domain.entity.convertor.HeldCollectionConvertor;
import com.beaker.mintcraft.collection.infrastructure.exception.CollectionException;
import com.beaker.mintcraft.collection.infrastructure.mapper.HeldCollectionMapper;
import com.beaker.mintcraft.lock.DistributeLock;
import com.beaker.mintcraft.mq.producer.StreamProducer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.beaker.mintcraft.cache.constant.CacheConstant.CACHE_KEY_SEPARATOR;
import static com.beaker.mintcraft.collection.infrastructure.exception.CollectionErrorCode.*;

/**
 * @Author beaker
 * @Date 2026/5/11 20:59
 * @Description 用户持有藏品服务
 */
@Service
public class HeldCollectionService extends ServiceImpl<HeldCollectionMapper, HeldCollection> {

    @Autowired
    private HeldCollectionStreamService heldCollectionStreamService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private StreamProducer streamProducer;

    private static final String HELD_COLLECTION_BIND_BOX_PREFIX = "HC:SALES:";

    @DistributeLock(keyExpression = "#request.serialNoBaseId", scene = "HELD_COLLECTION_CREATE")
    @Transactional(rollbackFor = Exception.class)
    public HeldCollection create(HeldCollectionCreateRequest request) {
        // 幂等校验
        HeldCollection existHeldCollection = queryByCollectionIdAndBizNo(request.getGoodsId(), request.getBizNo());
        if (existHeldCollection != null) {
            return existHeldCollection;
        }

        // HC:SALES:COLLECTION:1234
        HeldCollection heldCollection = new HeldCollection();
        Long serialNo = redissonClient.getAtomicLong(HELD_COLLECTION_BIND_BOX_PREFIX + request.getGoodsType() + CACHE_KEY_SEPARATOR + request.getSerialNoBaseId()).incrementAndGet();

        try {
            // 保存持有藏品
            heldCollection.init(request, serialNo.toString());
            boolean saveResult = this.save(heldCollection);

            if (!saveResult) {
                throw new CollectionException(HELD_COLLECTION_SAVE_FAILED);
            }

            // 保存成功, 写入流水
            HeldCollectionStream heldCollectionStream = new HeldCollectionStream().generateForCreate(heldCollection.getId(), request.getIdentifier());
            saveResult = heldCollectionStreamService.save(heldCollectionStream);
            Assert.isTrue(saveResult, () -> new CollectionException(HELD_COLLECTION_STREAM_SAVE_FAILED));

            return heldCollection;
        } catch (Throwable throwable) {
            //如果抛了异常, 并且数据库未更新成功过, 则回滚销量
            heldCollection = queryByCollectionIdAndBizNo(request.getGoodsId(), request.getBizNo());
            if (heldCollection == null) {
                redissonClient.getAtomicLong(HELD_COLLECTION_BIND_BOX_PREFIX + request.getGoodsType() + CACHE_KEY_SEPARATOR + request.getSerialNoBaseId()).decrementAndGet();
                return null;
            }

            return heldCollection;
        }
    }

    public Boolean active(HeldCollectionActiveRequest request) {
        HeldCollection heldCollection = getById(request.getHeldCollectionId());
        if (heldCollection == null) {
            throw new CollectionException(HELD_COLLECTION_QUERY_FAIL);
        }

        // 幂等校验
        if (heldCollection.getState().equals(HeldCollectionState.ACTIVED)) {
            return true;
        }

        heldCollection.actived(request.getNftId(), request.getTxHash());
        HeldCollectionStream heldCollectionStream = new HeldCollectionStream().generateForActive(heldCollection.getId(), request.getIdentifier());

        // 用编程式事务替代声明式事务, 避免 MQ 发送消息超时导致事务回滚
        transactionTemplate.executeWithoutResult(status -> {
            boolean result = updateById(heldCollection);
            Assert.isTrue(result, () -> new CollectionException(HELD_COLLECTION_SAVE_FAILED));
            result = heldCollectionStreamService.save(heldCollectionStream);
            Assert.isTrue(result, () -> new CollectionException(HELD_COLLECTION_STREAM_SAVE_FAILED));
        });

        // 操作完成后发送一条消息
        if (heldCollection.getBizType() != GoodsSaleBizType.AIR_DROP) {
            sendMsg(heldCollection, request.getEventType());
        }

        return true;
    }

    public long queryHeldCollectionCount(String userId) {
        QueryWrapper<HeldCollection> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        return this.count(wrapper);
    }

    @Cached(name = ":held_collection:cache:id:", expire = 60, localExpire = 10, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH, key = "args[0]", cacheNullValue = true)
    @CacheRefresh(refresh = 50, timeUnit = TimeUnit.MINUTES)
    public HeldCollection queryById(Long heldCollectionId) {
        return getById(heldCollectionId);
    }

    public PageResponse<HeldCollection> pageQueryByState(HeldCollectionPageQueryRequest request) {
        Page<HeldCollection> page = new Page<>();
        QueryWrapper<HeldCollection> wrapper = new QueryWrapper<>();

        // 包装查询条件
        wrapper.eq("user_id", request.getUserId());
        if (StringUtils.isNotBlank(request.getState())) {
            wrapper.eq("state", request.getState());
        }
        if (StringUtils.isNotBlank(request.getKeyword())) {
            wrapper.like("name", request.getKeyword());
        }
        wrapper.orderBy(true, false, "gmt_create");

        // 分页查询
        Page<HeldCollection> collections = this.page(page, wrapper);

        return PageResponse.of(collections.getRecords(), (int) collections.getTotal(), request.getPageSize(), request.getCurrentPage());
    }

    public HeldCollection queryByCollectionIdAndBizNo(Long collectionId, String bizNo) {
        QueryWrapper<HeldCollection> queryWrapper = new QueryWrapper<>();

        // 包装查询条件
        queryWrapper.eq("collection_id", collectionId);
        queryWrapper.eq("biz_no", bizNo);
        List<HeldCollection> retList = list(queryWrapper);
        if (CollectionUtils.isEmpty(retList)) {
            return null;
        }

        return retList.getFirst();
    }

    public HeldCollection queryByCollectionIdAndSerialNo(Long collectionId, String serialNo) {
        QueryWrapper<HeldCollection> queryWrapper = new QueryWrapper<>();

        // 包装查询条件
        queryWrapper.eq("collection_id", collectionId);
        queryWrapper.eq("serial_no", serialNo);
        List<HeldCollection> retList = list(queryWrapper);
        if (CollectionUtils.isEmpty(retList)) {
            return null;
        }

        return retList.getFirst();
    }

    private boolean sendMsg(HeldCollection heldCollection, HeldCollectionEventType eventType) {
        HeldCollectionDTO heldCollectionDTO = HeldCollectionConvertor.INSTANCE.mapToDto(heldCollection);

        //消息监听：HeldCollectionMsgListener
        return streamProducer.send("heldCollection-out-0", eventType.name(), JSON.toJSONString(heldCollectionDTO));
    }

}
