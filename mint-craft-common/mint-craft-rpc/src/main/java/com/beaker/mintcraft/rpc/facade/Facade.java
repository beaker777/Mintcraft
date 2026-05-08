package com.beaker.mintcraft.rpc.facade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author beaker
 * @Date 2026/4/27 20:12
 * @Description Facade 注解, 配合切面类使用
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Facade {
}
