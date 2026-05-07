package com.beaker.mintcraft.rpc.facade;

import com.alibaba.fastjson2.JSON;
import com.beaker.mintcraft.base.exception.biz.BizException;
import com.beaker.mintcraft.base.exception.system.SystemException;
import com.beaker.mintcraft.base.response.BaseResponse;
import com.beaker.mintcraft.base.response.ResponseCode;
import com.beaker.mintcraft.base.utils.BeanValidator;
import jakarta.validation.ValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Author beaker
 * @Date 2026/4/27 19:34
 * @Description Facade 切面
 */
@Aspect
@Component
@Order(Integer.MIN_VALUE)
public class FacadeAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacadeAspect.class);

    @Around("@annotation(com.beaker.mintcraft.rpc.service.Facade)")
    public Object facade(ProceedingJoinPoint pjp) throws Exception {
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();
        Class returnType = ((MethodSignature) pjp.getSignature()).getMethod().getReturnType();

        // 校验参数
        for (Object parameter : args) {
            try {
                BeanValidator.validateObject(parameter);
            } catch (ValidationException e) {
                printErrorLog(stopWatch, method, args, "failed to validate", null, e);
            }
        }

        try {
            // 执行方法
            Object response = pjp.proceed();
            enrichObject(response);
            printInfoLog(stopWatch, method, args, "end to execute", response, null);
            return response;
        } catch (Throwable throwable) {
            // 如果执行异常, 返回 failResponse
            printErrorLog(stopWatch, method, args, "failed to execute", null, throwable);
            return getFailedResponse(returnType, throwable);
        }
    }

    /**
     * 统一格式输出，方便做日志统计
     * <p>
     * *** 如果调整此处的格式，需要同步调整日志监控 ***
     *
     * @param action    行为
     * @param stopWatch 耗时
     * @param method    方法
     * @param args      参数
     * @param response  响应
     * @return 拼接后的字符串
     */
    private String getInfoMessage(String action, StopWatch stopWatch, Method method, Object[] args, Object response,
                                  Throwable exception) {

        StringBuilder stringBuilder = new StringBuilder(action);
        stringBuilder.append(" ,method = ");
        stringBuilder.append(method.getName());
        stringBuilder.append(" ,cost = ");
        stringBuilder.append(stopWatch.getTotalTimeMillis()).append(" ms");
        if (response instanceof BaseResponse) {
            stringBuilder.append(" ,success = ");
            stringBuilder.append(((BaseResponse) response).getSuccess());
        }
        if (exception != null) {
            stringBuilder.append(" ,success = ");
            stringBuilder.append(false);
        }
        stringBuilder.append(" ,args = ");
        stringBuilder.append(JSON.toJSONString(Arrays.toString(args)));

        if (response != null) {
            stringBuilder.append(" ,resp = ");
            stringBuilder.append(JSON.toJSONString(response));
        }

        if (exception != null) {
            stringBuilder.append(" ,exception = ");
            stringBuilder.append(exception.getMessage());
        }

        if (response instanceof BaseResponse) {
            BaseResponse baseResponse = (BaseResponse) response;
            if (!baseResponse.getSuccess()) {
                stringBuilder.append(" , execute_failed");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 日志打印 (正常日志)
     *
     * @param stopWatch
     * @param method
     * @param args
     * @param action
     * @param response
     */
    private void printInfoLog(StopWatch stopWatch, Method method, Object[] args, String action, Object response,
                              Throwable throwable) {
        try {
            //因为此处有JSON.toJSONString，可能会有异常，需要进行捕获，避免影响主干流程
            LOGGER.info(getInfoMessage(action, stopWatch, method, args, response, throwable), throwable);
            // 如果校验失败，则返回一个失败的response
        } catch (Exception e1) {
            LOGGER.error("log failed", e1);
        }
    }

    /**
     * 日志打印 (错误日志)
     *
     * @param stopWatch
     * @param method
     * @param args
     * @param action
     * @param response
     */
    private void printErrorLog(StopWatch stopWatch, Method method, Object[] args, String action, Object response,
                               Throwable throwable) {
        try {
            //因为此处有JSON.toJSONString，可能会有异常，需要进行捕获，避免影响主干流程
            LOGGER.error(getInfoMessage(action, stopWatch, method, args, response, throwable), throwable);
            // 如果校验失败，则返回一个失败的response
        } catch (Exception e1) {
            LOGGER.error("log failed", e1);
        }
    }

    /**
     * 将response的信息补全，主要是code和message
     *
     * @param response
     */
    private void enrichObject(Object response) {
        if (response instanceof BaseResponse) {
            if (((BaseResponse) response).getSuccess()) {
                //如果状态是成功的，需要将未设置的 responseCode 设置成 SUCCESS
                if (StringUtils.isEmpty(((BaseResponse) response).getResponseCode())) {
                    ((BaseResponse) response).setResponseCode(ResponseCode.SUCCESS.name());
                }
            } else {
                //如果状态是未成功的，需要将未设置的 responseCode 设置成 BIZ_ERROR
                if (StringUtils.isEmpty(((BaseResponse) response).getResponseCode())) {
                    ((BaseResponse) response).setResponseCode(ResponseCode.BIZ_ERROR.name());
                }
            }
        }
    }

    /**
     * 定义并返回一个通用的失败响应
     */
    private Object getFailedResponse(Class returnType, Throwable throwable) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //如果返回值的类型为 BaseResponse 的子类，则创建一个通用的失败响应
        if (returnType.getDeclaredConstructor().newInstance() instanceof BaseResponse) {
            BaseResponse response = (BaseResponse) returnType.getDeclaredConstructor().newInstance();
            response.setSuccess(false);
            if (throwable instanceof BizException bizException) {
                response.setResponseMessage(bizException.getErrorCode().getMessage());
                response.setResponseCode(bizException.getErrorCode().getCode());
            } else if (throwable instanceof SystemException systemException) {
                response.setResponseMessage(systemException.getErrorCode().getMessage());
                response.setResponseCode(systemException.getErrorCode().getCode());
            } else {
                response.setResponseMessage(throwable.toString());
                response.setResponseCode(ResponseCode.BIZ_ERROR.name());
            }

            return response;
        }

        // 如果返回值的类型不是 BaseResponse 的子类说明获取返回值失败
        LOGGER.error(
                "failed to getFailedResponse , returnType (" + returnType + ") is not instanceof BaseResponse");
        return null;
    }
}
