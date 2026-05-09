package com.beaker.mintcraft.user.domain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.beaker.mintcraft.base.utils.HttpUtils;
import com.beaker.mintcraft.user.domain.service.AuthService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;

import java.util.Map;

/**
 * @Author beaker
 * @Date 2026/5/9 16:17
 * @Description 实名认证实现类
 */
@Slf4j
public class AuthServiceImpl implements AuthService {

    private String host;

    private String path;

    private String appcode;

    private static final String STATE = "state";

    public AuthServiceImpl(String host, String path, String appcode) {
        this.host = host;
        this.path = path;
        this.appcode = appcode;
    }

    @Override
    public boolean checkAuth(String realName, String idCard) {
        String method = "POST";

        // header
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(2);
        // Authorization:APPCODE xxx
        headers.put("Authorization", "APPCODE " + appcode);
        // 根据要求，定义相对应的 Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        // body
        Map<String, String> bodies = Maps.newHashMapWithExpectedSize(2);
        bodies.put("id_number", idCard);
        bodies.put("name", realName);

        // query
        Map<String, String> queries = Maps.newHashMapWithExpectedSize(2);

        try {
            var response = HttpUtils.doPost(host, path, method, headers, queries, bodies);
            Map<String, Object> resultMap = JSON.parseObject(EntityUtils.toString(response.getEntity()), Map.class);
            log.info("auth result : " + resultMap);
            if ((Integer)resultMap.get(STATE) == 1) {
                return true;
            }
        } catch (Exception e) {
            log.error("checkAuth error realName=" + realName, e);
        }
        return false;

    }

}
