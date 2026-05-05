package com.beaker.mintcraft.gateway.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.beaker.mintcraft.api.user.constant.UserPermission;
import com.beaker.mintcraft.api.user.constant.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/5/5 16:13
 * @Description sa-token 配置类
 */
@Configuration
@Slf4j
public class SaTokenConfiguration {

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")
                // 排除地址
                .addExclude("/favicon.ico")
                // 鉴权方法
                .setAuth(obj -> {
                    // 登录校验: 拦截所有路由, 排除 auth/login 用于登录
                    SaRouter.match("/**")
                            .notMatch("/auth/**", "/collection/collectionList", "/collection/collectionInfo", "/wxPay/**")
                            .check(r -> StpUtil.checkLogin());

                    // 权限校验: 不同模块校验不同权限
                    SaRouter.match("/admin/**", r -> StpUtil.checkRole(UserRole.ADMIN.name()));

                    SaRouter.match("/trade/**", r -> StpUtil.checkPermission(UserPermission.AUTH.name()));

                    SaRouter.match("/user/**", r -> StpUtil.checkPermissionOr(UserPermission.BASIC.name(), UserPermission.FROZEN.name()));
                    SaRouter.match("/order/**", r -> StpUtil.checkPermissionOr(UserPermission.BASIC.name(),UserPermission.FROZEN.name()));
                })
                // 异常方法
                .setError(this::getSaResult);
    }

    private SaResult getSaResult(Throwable throwable) {
        switch (throwable) {
            case NotLoginException notLoginException:
                log.error("请先登录");
                return SaResult.error("请先登录");
            case NotRoleException notRoleException:
                if (UserRole.ADMIN.name().equals(notRoleException.getRole())) {
                    log.error("请勿越权使用！");
                    return SaResult.error("请勿越权使用！");
                }
                log.error("您无权限进行此操作！");
                return SaResult.error("您无权限进行此操作！");
            case NotPermissionException notPermissionException:
                if (UserPermission.AUTH.name().equals(notPermissionException.getPermission())) {
                    log.error("请先完成实名认证！");
                    return SaResult.error("请先完成实名认证！");
                }
                log.error("您无权限进行此操作！");
                return SaResult.error("您无权限进行此操作！");
            default:
                return SaResult.error(throwable.getMessage());
        }
    }
}
