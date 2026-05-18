package com.beaker.mintcraft.web.filter;

import com.beaker.mintcraft.web.util.TokenUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * @Author beaker
 * @Date 2026/5/18 20:08
 * @Description token 过滤器
 */
public class TokenFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    public static final ThreadLocal<String> TOKEN_THREAD_LOCAL = new ThreadLocal<>();

    public static final ThreadLocal<Boolean> STRESS_THREAD_LOCAL = new ThreadLocal<>();

    private static final String HEADER_VALUE_NULL = "null";

    private static final String HEADER_VALUE_UNDEFINED = "undefined";

    private RedissonClient redissonClient;

    public TokenFilter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            String token = httpRequest.getHeader("Authorization");
            boolean isStress = BooleanUtils.toBoolean(httpRequest.getHeader("isStress"));

            // 校验请求是否携带 token
            if (token == null || HEADER_VALUE_NULL.equals(token) || HEADER_VALUE_UNDEFINED.equals(token)) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("No token found ...");

                logger.error("no token found in header, pls check !");

                return;
            }

            // 校验 token 有效性
            boolean isValid = checkTokenValidity(token, isStress);

            if (!isValid) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Invalid or expired token");

                logger.error("token validate failed, pls check !");

                return;
            }

            // token 有效, 继续执行其他过滤器链
            chain.doFilter(request, response);
        } finally {
            TOKEN_THREAD_LOCAL.remove();
            STRESS_THREAD_LOCAL.remove();
        }
    }

    private boolean checkTokenValidity(String token, Boolean isStress) {
        String result;

        if (isStress) {
            //如果是压测，则生成一个随机数，模拟 token
            result = UUID.randomUUID().toString();
            STRESS_THREAD_LOCAL.set(isStress);
        } else {
            String tokenKey = TokenUtil.getTokenKeyByValue(token);

            String luaScript = """
                local value = redis.call('GET', KEYS[1])
                
                if value ~= ARGV[1] then
                    return redis.error_reply('token not valid')
                end
                
                redis.call('DEL', KEYS[1])
                
                return value
                """;

            try {
                result = (String) redissonClient.getScript().eval(
                        RScript.Mode.READ_WRITE,
                        luaScript,
                        RScript.ReturnType.STATUS,
                        Arrays.asList(tokenKey),
                        token);
            } catch (RedisException e) {
                logger.error("check token failed", e);
                return false;
            }
        }

        TOKEN_THREAD_LOCAL.set(result);

        return result != null;
    }

}
