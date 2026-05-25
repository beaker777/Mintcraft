package com.beaker.mintcraft.chain.domain.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.beaker.mintcraft.api.chain.constant.ChainOperateType;
import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainCreateData;
import com.beaker.mintcraft.api.chain.response.data.ChainOperationData;
import com.beaker.mintcraft.base.exception.biz.RepoErrorCode;
import com.beaker.mintcraft.base.exception.system.SystemException;
import com.beaker.mintcraft.base.utils.BeanValidator;
import com.beaker.mintcraft.chain.domain.constant.ChainCode;
import com.beaker.mintcraft.chain.domain.entity.ChainOperateInfo;
import com.beaker.mintcraft.chain.domain.entity.ChainRequest;
import com.beaker.mintcraft.chain.domain.response.ChainResponse;
import com.beaker.mintcraft.limiter.SlidingWindowRateLimiter;
import com.beaker.mintcraft.mq.producer.StreamProducer;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * @Author beaker
 * @Date 2026/5/24 18:10
 * @Description 链服务模板
 */
@Slf4j
public abstract class AbstractChainService implements ChainService {

    @Autowired
    private ChainOperateInfoService chainOperateInfoService;

    @Autowired
    private SlidingWindowRateLimiter slidingWindowRateLimiter;

    @Autowired
    private StreamProducer streamProducer;

    private static ThreadFactory chainResultProcessFactory = new ThreadFactoryBuilder()
            .setNameFormat("chain-result-process-pool-%d")
            .build();

    ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(10, chainResultProcessFactory);

    protected ChainProcessResponse doPostExecute(ChainProcessRequest chainProcessRequest, ChainOperateType chainOperateTypeEnum,
                                                 Consumer<ChainRequest> consumer) {
        return handle(chainProcessRequest, request -> {
            // 每分钟最多发起一次请求
            Boolean rateLimitResult = slidingWindowRateLimiter.tryAcquire(
                    "limit#" + chainProcessRequest.getBizType() + chainProcessRequest.getIdentifier(), 1, 60);

            // 限流器拦截
            if (!rateLimitResult) {
                return new ChainProcessResponse.Builder()
                        .responseCode(ChainCode.PROCESSING.name())
                        .data(new ChainOperationData(chainProcessRequest.getIdentifier()))
                        .buildSuccess();
            }

            // 幂等校验
            ChainOperateInfo chainOperateInfo = chainOperateInfoService
                    .queryByOutBizId(chainProcessRequest.getBizId(), chainProcessRequest.getBizType(), chainProcessRequest.getIdentifier());
            if (chainOperateInfo != null) {
                // 返回幂等结果
                return duplicateResponse(chainProcessRequest, chainOperateTypeEnum, chainOperateInfo);
            }

            ChainRequest chainRequest = new ChainRequest();

            // 保存流水
            Long operateInfoId = chainOperateInfoService.insertInfo(chainType(),
                    chainProcessRequest.getBizId(), chainProcessRequest.getBizType(), chainOperateTypeEnum.name(),
                    JSON.toJSONString(chainProcessRequest), chainProcessRequest.getIdentifier());

            // 核心逻辑执行
            consumer.accept(chainRequest);

            // 发送 post 请求
            ChainResponse result = doPost(chainRequest);
            log.info("wen chang post result:{}", JSON.toJSONString(result));

            // 更新流水
            boolean updateResult = chainOperateInfoService.updateResult(operateInfoId, null,
                    result.getSuccess() ? result.getData().toString() : result.getError().toString());
            if (!updateResult) {
                throw new SystemException(RepoErrorCode.UPDATE_FAILED);
            }

            ChainProcessResponse response = buildResult(result, chainProcessRequest, chainOperateTypeEnum);
            if (response.getSuccess() && chainOperateTypeEnum != ChainOperateType.USER_CREATE) {
                //延迟5秒钟之后查询状态并发送 MQ 消息通知上游
                scheduler.schedule(() -> {
                    try {
                        ChainOperateInfo operateInfo = chainOperateInfoService.queryByOutBizId(
                                chainProcessRequest.getBizId(), chainProcessRequest.getBizType(), chainProcessRequest.getIdentifier());

                        // TODO: 后续补充发送 MQ 消息
                    } catch (Exception e) {
                        log.error("query chain result failed,", e);
                    }
                }, 5, TimeUnit.SECONDS);
            }

            return response;
        });
    }

    public static <T, R extends ChainProcessResponse> ChainProcessResponse handle(T request, Function<T, R> function) {
        // 请求不为空, 且要满足注解
        requireNonNull(request);
        BeanValidator.validateObject(request);

        return function.apply(request);
    }

    private ChainProcessResponse duplicateResponse(ChainProcessRequest chainProcessRequest, ChainOperateType chainOperateTypeEnum, ChainOperateInfo chainOperateInfo) {
        if (chainOperateTypeEnum == ChainOperateType.USER_CREATE) {
            JSONObject jsonObject = JSON.parseObject(chainOperateInfo.getResult(), JSONObject.class);
            String blockChainAddr = (String) jsonObject.get("native_address");
            String blockChainName = chainProcessRequest.getUserId();
            return new ChainProcessResponse.Builder()
                    .responseCode(ChainCode.SUCCESS.name())
                    .data(new ChainCreateData(chainProcessRequest.getIdentifier(), blockChainAddr, blockChainName, chainType()))
                    .buildSuccess();
        } else {
            return new ChainProcessResponse.Builder()
                    .responseCode(ChainCode.PROCESSING.name())
                    .data(new ChainOperationData(chainProcessRequest.getIdentifier()))
                    .buildSuccess();
        }
    }

    /**
     * 结果构造
     *
     * @param result
     * @param chainProcessRequest
     * @param chainOperateTypeEnum
     * @return
     */
    private ChainProcessResponse buildResult(ChainResponse result, ChainProcessRequest chainProcessRequest, ChainOperateType chainOperateTypeEnum) {
        if (result.getSuccess()) {
            if (chainOperateTypeEnum == ChainOperateType.USER_CREATE) {
                JSONObject dataJsonObject = result.getData();
                String blockChainAddr = (String) dataJsonObject.get("native_address");
                String blockChainName = chainProcessRequest.getUserId();
                return new ChainProcessResponse.Builder()
                        .data(new ChainCreateData(chainProcessRequest.getIdentifier(), blockChainAddr, blockChainName, chainType()))
                        .buildSuccess();
            } else {
                return new ChainProcessResponse.Builder()
                        .responseCode(ChainCode.PROCESSING.name())
                        .data(new ChainOperationData(chainProcessRequest.getIdentifier()))
                        .buildSuccess();
            }

        }

        return new ChainProcessResponse.Builder()
                .responseCode(result.getResponseCode())
                .responseMessage(result.getResponseMessage())
                .buildFailed();
    }


    /**
     * 执行post方法
     *
     * @param chainRequest
     * @return
     */
    protected abstract ChainResponse doPost(ChainRequest chainRequest);

    /**
     * 执行delete方法
     *
     * @param chainRequest
     * @return
     */
    protected abstract ChainResponse doDelete(ChainRequest chainRequest);

    /**
     * 执行get方法
     *
     * @param chainRequest
     * @return
     */
    protected abstract ChainResponse doGetQuery(ChainRequest chainRequest);


    /**
     * 返回chainType
     *
     * @return
     */
    protected abstract String chainType();
}
