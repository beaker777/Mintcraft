package com.beaker.mintcraft.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.goods.valobj.BaseGoodsVO;
import com.beaker.mintcraft.auth.constant.TokenScene;
import com.beaker.mintcraft.auth.exception.AuthErrorCode;
import com.beaker.mintcraft.auth.exception.AuthException;
import com.beaker.mintcraft.web.util.TokenUtil;
import com.beaker.mintcraft.web.vo.Result;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.beaker.mintcraft.auth.exception.AuthErrorCode.*;
import static com.beaker.mintcraft.cache.constant.CacheConstant.CACHE_KEY_SEPARATOR;
import static com.beaker.mintcraft.web.util.TokenUtil.TOKEN_PREFIX;

/**
 * @Author beaker
 * @Date 2026/5/18 16:12
 * @Description token 发放接口
 */
@Slf4j
@RestController
@RequestMapping("/token")
public class TokenController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @DubboReference
    private GoodsFacadeService goodsFacadeService;

    @GetMapping("/get")
    public Result<String> get(@NotBlank String scene, @NotBlank String key) {
        // fixme: 这里并没有校验 token 对应的商品和下单的商品是不是一个
        TokenScene tokenScene = Arrays
                .stream(TokenScene.values())
                .filter(tokenSceneEnum -> tokenSceneEnum.getScene().equals(scene))
                .findFirst()
                .orElseThrow(() -> new AuthException(TOKEN_SCENE_NOT_EXIST));

        // 校验商品是否存在且可售
        BaseGoodsVO baseGoodsVO = goodsFacadeService.getGoods(key, getGoodsType(tokenScene));
        if (baseGoodsVO == null || !baseGoodsVO.available()) {
            throw new AuthException(TOKEN_KEY_IS_ILLEGAL);
        }

        if (StpUtil.isLogin()) {
            String userId = (String) StpUtil.getLoginId();
            // 获取 token key value
            // key: token:buy:29:10085
            // value: YZdkYfQ8fy7biSTsS5oZrbsB8eN7dHPgtCV0dw/36AHSfDQzWOj+ULNEcMluHvep/txjP+BqVRH3JlprS8tWrQ==
            String tokenKey = TOKEN_PREFIX + scene + CACHE_KEY_SEPARATOR + userId + CACHE_KEY_SEPARATOR + key;
            String tokenValue = TokenUtil.getTokenValueByKey(tokenKey);

            // 将 key value 保存进 redis 30 min 过期
            stringRedisTemplate.opsForValue().set(tokenKey, tokenValue, 30, TimeUnit.MINUTES);
            return Result.success(tokenValue);
        }

        throw new AuthException(USER_NOT_LOGIN);
    }

    private GoodsType getGoodsType(TokenScene tokenScene) {
        return switch (tokenScene) {
            case BUY_COLLECTION -> GoodsType.COLLECTION;
            case BUY_BLIND_BOX -> GoodsType.BLIND_BOX;
            default -> throw new AuthException(TOKEN_SCENE_NOT_EXIST);
        };
    }
}
