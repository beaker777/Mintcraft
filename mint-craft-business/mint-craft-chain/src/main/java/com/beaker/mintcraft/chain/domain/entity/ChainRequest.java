package com.beaker.mintcraft.chain.domain.entity;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author beaker
 * @Date 2026/5/25 20:39
 * @Description 链请求
 */
@Data
public class ChainRequest {

    /**
     * 签名
     */
    private String signature;

    /**
     * 域名
     */
    private String host;

    /**
     * 当前时间
     */
    private Long currentTime;

    /**
     * 请求体
     */
    private RequestBody body;

    /**
     * 请求路径
     */
    private String path;

    public ChainRequest build(RequestBody body, String path, String signature, String host, Long currentTime) {
        this.setSignature(signature);
        this.setHost(host);
        this.setBody(body);
        this.setPath(path);
        this.setCurrentTime(currentTime);
        return this;
    }
}
