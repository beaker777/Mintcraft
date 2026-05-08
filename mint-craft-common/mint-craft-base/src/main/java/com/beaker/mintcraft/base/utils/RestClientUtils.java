package com.beaker.mintcraft.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Author beaker
 * @Date 2026/5/8 17:56
 * @Description RestClient 工具类
 */
@Slf4j
public class RestClientUtils {

    public static HttpHeaders configureHeaders(HttpHeaders headers, Map headersMap) {
        // 将 Map 转化为 header
        headersMap.forEach((k, v) -> headers.add(k.toString(), v.toString()));
        return headers;
    }

    private static String buildUrl(String path, Map<String, String> querys) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();

        // 先将 path 拼接进来
        if (!StringUtils.isBlank(path)) {
            sbUrl.append(path);
        }

        // 将 query 逐个拼接进来 -> ?mobile=xxx&code=1234
        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (!sbQuery.isEmpty()) {
                    sbQuery.append("&");
                }

                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append("=");
                        sbQuery.append(URLEncoder.encode(query.getValue(), StandardCharsets.UTF_8));
                    }
                }
            }

            if (!sbQuery.isEmpty()) {
                sbUrl.append("?").append(sbQuery);
            }
        }

        // 返回最终的 url
        return sbUrl.toString();
    }

    public static ResponseEntity doPost(String host, String path, Map<String, String> headersMap,
                                        Map<String, String> querys,
                                        Map<String, String> bodys) throws Exception {
        RestClient restClient = RestClient.builder()
                .baseUrl(host)
                .build();

        var result = restClient.post()
                .uri(buildUrl(path, querys))
                .headers(headers -> configureHeaders(headers, headersMap))
                .body(bodys)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("http client error, request: {}, response: {}", request, response);
                }).onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    log.error("http server error, request: {}, response: {}", request, response);
                }).toBodilessEntity();

        return result;
    }
}
