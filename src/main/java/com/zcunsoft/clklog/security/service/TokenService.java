package com.zcunsoft.clklog.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zcunsoft.clklog.common.constant.Constants;
import com.zcunsoft.clklog.common.model.LoginUser;
import com.zcunsoft.clklog.common.utils.ObjectMapperUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
 */
@Component
public class TokenService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    @Resource
    private StringRedisTemplate queueRedisTemplate;

    @Resource
    private ObjectMapperUtil objectMapper;

    /**
     * API Key请求头名称
     */
    private static final String API_KEY_HEADER = "X-API-Key";


    /**
     * Redis Key前缀
     */
    private static final String API_KEY_PREFIX = "clklog:apikey:";

    private final TypeReference<LoginUser> loginUserTypeReference = new TypeReference<LoginUser>() {
    };

    /**
     * 获取用户身份信息
     *
     * @param request Http请求
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        String token = getToken(request);
        if (StringUtils.isNotBlank(token)) {
            if (!isValidJwtToken(token)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid JWT format: token does not contain exactly 2 period characters");
                }
                return null;
            }
            try {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
                String userInfo = queueRedisTemplate.opsForValue().get(userKey);
                if (StringUtils.isNotEmpty(userInfo)) {
                    LoginUser user = objectMapper.readValue(userInfo, loginUserTypeReference);
                    return user;
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return null;
    }

    /**
     * 删除用户身份信息
     *
     * @param token 用户令牌
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = getTokenKey(token);
            queueRedisTemplate.delete(userKey);
        }
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser 用户信息
     */
    public void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        String userInfo = null;
        try {
            userInfo = objectMapper.writeValueAsString(loginUser);
            queueRedisTemplate.opsForValue().set(userKey, userInfo, expireTime, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isValidJwtToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        int periodCount = 0;
        for (char c : token.toCharArray()) {
            if (c == '.') {
                periodCount++;
            }
        }
        return periodCount == 2;
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return Constants.LOGIN_TOKEN_KEY + uuid;
    }

    public LoginUser getLoginUserByApiKey(HttpServletRequest request) {

        LoginUser loginUser = null;
        String apiKey = request.getHeader(API_KEY_HEADER);

        // 如果请求头中没有API Key，直接放行，让后续过滤器处理
        if (apiKey != null) {

            if (logger.isDebugEnabled()) {
                logger.debug("Received API Key authentication request: {}", maskApiKey(apiKey));
            }
            // 验证API Key（直接在过滤器中实现验证逻辑）
            loginUser = validateApiKey(apiKey);
        }
        return loginUser;
    }

    private LoginUser validateApiKey(String apiKey) {
        LoginUser loginUser = null;
        if (apiKey == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("API Key or Secret is null");
            }
        } else {
            String key = API_KEY_PREFIX + apiKey;

            // 获取缓存的API Key信息
            String userInfo = queueRedisTemplate.opsForValue().get(key);
            if (StringUtils.isBlank(userInfo)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("API Key cache is empty: {}", maskApiKey(apiKey));
                }
                return null;
            }

            try {
                loginUser = objectMapper.readValue(userInfo, loginUserTypeReference);
            } catch (JsonProcessingException e) {
                logger.error("Failed to parse API Key user info: {}", maskApiKey(apiKey), e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("API Key validation successful: {}", maskApiKey(apiKey));
            }
        }
        return loginUser;
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 6) {
            return apiKey;
        }
        return apiKey.substring(0, 6) + "****";
    }
}
