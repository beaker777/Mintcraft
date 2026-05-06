package com.beaker.mintcraft.user.infrastructure.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;

/**
 * @Author beaker
 * @Date 2026/5/6 20:34
 * @Description AES 加解密工具类
 */
public class AesUtil {

    private final static String key = "v9J3mX/7L2sZ1bN5cV4xQ8A6dF0gH3jK9pT4rW2yE8o=";
    private final static AES aes = SecureUtil.aes(Base64.getDecoder().decode(key));

    public static String encrypt(String data) {
        // 判空
        if (StringUtils.isBlank(data)) {
            return data;
        }

        return aes.encryptHex(data);
    }

    public static String decrypt(String data) {
        // 判空
        if (StringUtils.isBlank(data)) {
            return data;
        }

        return aes.decryptStr(data);
    }
}