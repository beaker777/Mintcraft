package com.beaker.mintcraft.rpc.support;

import com.alibaba.fastjson2.JSON;
import com.beaker.mintcraft.base.exception.system.RemoteCallException;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import static com.beaker.mintcraft.base.exception.system.SystemErrorCode.REMOTE_CALL_RESPONSE_IS_FAILED;
import static com.beaker.mintcraft.base.exception.system.SystemErrorCode.REMOTE_CALL_RESPONSE_IS_NULL;

/**
 * @Author beaker
 * @Date 2026/4/26 21:36
 * @Description 远程调用包装类
 */
public class RemoteCallWrapper {

    public static Logger logger = LoggerFactory.getLogger(RemoteCallWrapper.class);

    private static ImmutableSet<String> SUCCESS_CHECK_METHOD = ImmutableSet.of("isSuccess", "isSucceeded",
            "getSuccess");

    private static ImmutableSet<String> SUCCESS_CODE_METHOD = ImmutableSet.of("getResponseCode");

    private static ImmutableSet<String> SUCCESS_CODE = ImmutableSet.of("SUCCESS", "DUPLICATE",
            "DUPLICATED_REQUEST");

    public static <T, R> R call(Function<T, R> function, T request, boolean checkResponse) {
        return call(function, request, request.getClass().getSimpleName(), checkResponse, false);
    }


    public static <T, R> R call(Function<T, R> function, T request) {
        return call(function, request, request.getClass().getSimpleName(), true, false);
    }

    public static <T, R> R call(Function<T, R> function, T request, String requestName) {
        return call(function, request, requestName, true, false);
    }

    public static <T, R> R call(Function<T, R> function, T request, String requestName,boolean checkResponse) {
        return call(function, request, requestName, checkResponse, false);
    }

    public static <T, R> R call(Function<T, R> function, T request, boolean checkResponse, boolean checkResponseCode) {
        return call(function, request, request.getClass().getSimpleName(), checkResponse, checkResponseCode);
    }

    public static <T, R> R call(Function<T, R> function, T request, String requestName, boolean checkResponse,
                                boolean checkResponseCode) {
        StopWatch stopWatch = new StopWatch();
        R response = null;

        try {
            stopWatch.start();
            response = function.apply(request);
            stopWatch.stop();

            // 如果需要 check response
            if (checkResponse) {
                Assert.notNull(response, REMOTE_CALL_RESPONSE_IS_NULL.name());

                if (!isResponseValid(response)) {
                      logger.error("Response Invalid on Remote Call. request {}, response {}",
                              JSON.toJSONString(request), JSON.toJSONString(response));

                      throw new RemoteCallException(JSON.toJSONString(response), REMOTE_CALL_RESPONSE_IS_FAILED);
                }
            }

            // 如果需要 check responseCode
            if (checkResponseCode) {
                Assert.notNull(response, REMOTE_CALL_RESPONSE_IS_NULL.name());

                if (!isResponseCodeValid(response)) {
                    logger.error("Response Code Invalid on Remote Call. request {}, response {}",
                            JSON.toJSONString(request), JSON.toJSONString(response));

                    throw new RemoteCallException(JSON.toJSONString(response), REMOTE_CALL_RESPONSE_IS_FAILED);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("Catch Exception on Remote Call Reflection: " + e.getMessage(), e);
            throw new IllegalArgumentException("Catch Exception on Remote Call Reflection: " + e.getMessage(), e);
        } catch (Throwable e) {
            logger.error("request exception {}", JSON.toJSONString(request));
            logger.error("Catch Exception on Remote Call :" + e.getMessage(), e);
            throw e;
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info("## Method={} ,## 耗时={}ms ,## [请求报文]:{},## [响应报文]:{}",
                        requestName, stopWatch.getTotalTimeMillis(),
                        JSON.toJSONString(request), JSON.toJSONString(response));
            }
        }

        return response;
    }

    private static <R> boolean isResponseValid(R response) throws InvocationTargetException, IllegalAccessException {
        Method successMethod = null;
        Method[] methods = response.getClass().getMethods();

        // 获取到 response 类的 success 方法
        for (Method method : methods) {
            String name = method.getName();

            if (SUCCESS_CHECK_METHOD.contains(name)) {
                successMethod = method;
                break;
            }
        }

        // 没有 success 方法默认返回 true
        if (successMethod == null) {
            return true;
        }

        return (Boolean) successMethod.invoke(response);
    }

    private static <R> boolean isResponseCodeValid(R response) throws InvocationTargetException, IllegalAccessException {
        Method successMethod = null;
        Method[] methods = response.getClass().getMethods();

        // 获取到 response 类的 getResponseCode 方法
        for (Method method : methods) {
            String name = method.getName();

            if (SUCCESS_CODE_METHOD.contains(name)) {
                successMethod = method;
                break;
            }
        }

        // 没有 getResponseCode 方法默认返回 true
        if (successMethod == null) {
            return true;
        }

        // 检测 responseCode 是否在成功响应码的集合中
        return SUCCESS_CODE.contains(successMethod.invoke(response));
    }

}
