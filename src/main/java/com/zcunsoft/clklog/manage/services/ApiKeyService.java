package com.zcunsoft.clklog.manage.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zcunsoft.clklog.common.model.LoginUser;
import com.zcunsoft.clklog.common.model.UserInfo;
import com.zcunsoft.clklog.common.utils.ObjectMapperUtil;
import com.zcunsoft.clklog.common.utils.SecurityUtils;
import com.zcunsoft.clklog.manage.entity.mysql.TblApiKey;
import com.zcunsoft.clklog.manage.models.apikey.*;
import com.zcunsoft.clklog.manage.models.enums.EnableStatus;
import com.zcunsoft.clklog.manage.repository.mysql.ApiKeyRepository;
import com.zcunsoft.clklog.sysmgmt.domains.User;
import com.zcunsoft.clklog.sysmgmt.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * API Key服务（管理端）
 * 负责API Key的CRUD操作，并同步更新Redis缓存
 */
@Service
public class ApiKeyService {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyService.class);

    @Resource
    private ApiKeyRepository apiKeyRepository;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IUserService userService;

    @Resource
    private ObjectMapperUtil objectMapper;

    @PostConstruct
    public void initCache() {
        logger.info("Initializing API Key cache...");
        try {
            Specification<TblApiKey> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get("status"), EnableStatus.启用.name()));
                Predicate[] pre = new Predicate[predicates.size()];
                return cb.and(predicates.toArray(pre));
            };
            List<TblApiKey> apiKeyList = this.apiKeyRepository.findAll(spec);
            for (TblApiKey apiKey : apiKeyList) {
                cacheApiKey(apiKey);
            }
            logger.info("API Key cache initialized: {} keys loaded", Integer.valueOf(apiKeyList.size()));
        } catch (Exception e) {
            logger.error("Failed to initialize API Key cache", e);
        }
    }

    public void cacheApiKey(String apiKey, String username, Long expiresAt) {
        User user = userService.getByUserName(username);
        if (user != null) {
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(user, userInfo);
            LoginUser loginUser = new LoginUser(user.getUserId(), userInfo);
            loginUser.setExpireTime(System.currentTimeMillis());
            String key = "clklog:apikey:" + apiKey;

            try {
                String info = objectMapper.writeValueAsString(loginUser);

                this.stringRedisTemplate.opsForValue().set(key, info);
                if (expiresAt != null) {
                    this.stringRedisTemplate.expire(key, (expiresAt - System.currentTimeMillis()) / 1000, TimeUnit.SECONDS);
                } else {
                    this.stringRedisTemplate.persist(key);
                }
            } catch (JsonProcessingException e) {

            }
            if (logger.isDebugEnabled()) {
                logger.debug("Cached API Key: {}", apiKey);
            }
        }
    }

    public void cacheApiKey(TblApiKey apiKey) {
        try {
            Long expiresAt = null;
            if (apiKey.getExpiresAt() != null) {
                expiresAt = Long.valueOf(apiKey.getExpiresAt().getTime());
            }
            if (apiKey.getStatus().equalsIgnoreCase(EnableStatus.启用.name())) {
                cacheApiKey(apiKey.getApiKey(), apiKey.getUsername(), expiresAt);
            } else {
                removeApiKey(apiKey.getApiKey());
            }
        } catch (Exception e) {
            logger.error("Failed to cache API Key: {}", apiKey.getApiKey(), e);
        }
    }

    public void removeApiKey(String apiKey) {
        if (apiKey != null) {
            this.stringRedisTemplate.delete("clklog:apikey:" + apiKey);
            if (logger.isDebugEnabled()) {
                logger.debug("Removed API Key cache: {}", apiKey);
            }
        }
    }

    public AddApiKeyResponse addApiKey(AddApiKeyRequest request) {
        AddApiKeyResponse response = new AddApiKeyResponse();
        try {
            String userId = SecurityUtils.getUserId();
            String username = SecurityUtils.getUsername();

            String apiKey = generateApiKey();

            TblApiKey tblApiKey = new TblApiKey();
            tblApiKey.setId(UUID.randomUUID().toString());
            tblApiKey.setUserId(userId);
            tblApiKey.setUsername(username);
            tblApiKey.setApiKey(apiKey);
            tblApiKey.setDisplayName(request.getDisplayName());
            tblApiKey.setStatus(EnableStatus.启用.name());
            tblApiKey.setExpiresAt(request.getExpiresAt());
            tblApiKey.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            tblApiKey.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            apiKeyRepository.save(tblApiKey);

            // 同步更新Redis缓存
            cacheApiKey(tblApiKey);

            AddApiKeyResponseData responseData = new AddApiKeyResponseData();
            responseData.setId(tblApiKey.getId());
            responseData.setApiKey(apiKey);
            response.setData(responseData);

        } catch (Exception ex) {
            logger.error("addApiKey error", ex);
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }


    public GetApiKeyResponse getApiKey(GetApiKeyRequest request) {
        GetApiKeyResponse response = new GetApiKeyResponse();
        try {
            Optional<TblApiKey> optionalApiKey = apiKeyRepository.findById(request.getId());
            if (!optionalApiKey.isPresent()) {
                response.setCode(500);
                response.setMsg("密钥不存在");
                return response;
            }

            TblApiKey tblApiKey = optionalApiKey.get();
            ApiKeyModel model = convertToApiModel(tblApiKey);
            response.setData(model);

        } catch (Exception ex) {
            logger.error("getApiKey error", ex);
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }


    public EditApiKeyResponse editApiKey(EditApiKeyRequest request) {
        EditApiKeyResponse response = new EditApiKeyResponse();
        try {
            Optional<TblApiKey> optionalApiKey = apiKeyRepository.findById(request.getId());
            if (!optionalApiKey.isPresent()) {
                response.setCode(500);
                response.setMsg("密钥不存在");
                return response;
            }

            TblApiKey tblApiKey = optionalApiKey.get();

            if (request.getDisplayName() != null) {
                tblApiKey.setDisplayName(request.getDisplayName());
            }
            if (request.getStatus() != null) {
                tblApiKey.setStatus(request.getStatus());
            }
            if (request.getExpiresAt() != null) {
                tblApiKey.setExpiresAt(request.getExpiresAt());
            } else {
                tblApiKey.setExpiresAt(null);
            }
            tblApiKey.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            apiKeyRepository.save(tblApiKey);

            // 同步更新Redis缓存
            cacheApiKey(tblApiKey);

        } catch (Exception ex) {
            logger.error("editApiKey error", ex);
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }


    public DeleteApiKeyResponse deleteApiKey(DeleteApiKeyRequest request) {
        DeleteApiKeyResponse response = new DeleteApiKeyResponse();
        try {
            Optional<TblApiKey> optionalApiKey = apiKeyRepository.findById(request.getId());
            if (!optionalApiKey.isPresent()) {
                response.setCode(500);
                response.setMsg("密钥不存在");
                return response;
            }

            // 获取要删除的API Key值，用于删除缓存
            String apiKeyValue = optionalApiKey.get().getApiKey();

            apiKeyRepository.deleteById(request.getId());

            // 同步删除Redis缓存
            removeApiKey(apiKeyValue);

        } catch (Exception ex) {
            logger.error("deleteApiKey error", ex);
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }


    public ListApiKeyResponse listApiKey(ListApiKeyRequest request) {
        ListApiKeyResponse response = new ListApiKeyResponse();
        try {

            Specification<TblApiKey> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get("username"), SecurityUtils.getUsername()));
                Predicate[] pre = new Predicate[predicates.size()];
                return cb.and(predicates.toArray(pre));
            };
            List<TblApiKey> apiKeyList = apiKeyRepository.findAll(spec);

            List<ApiKeyModel> modelList = new ArrayList<>();
            for (TblApiKey tblApiKey : apiKeyList) {
                ApiKeyModel model = convertToApiModel(tblApiKey);
                modelList.add(model);
            }

            response.setData(modelList);

        } catch (Exception ex) {
            logger.error("listApiKey error", ex);
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    /**
     * 转换实体到模型
     */
    private ApiKeyModel convertToApiModel(TblApiKey tblApiKey) {
        ApiKeyModel model = new ApiKeyModel();
        model.setId(tblApiKey.getId());
        model.setUserId(tblApiKey.getUserId());
        model.setUsername(tblApiKey.getUsername());
        model.setApiKey(maskApiKey(tblApiKey.getApiKey()));
        model.setDisplayName(tblApiKey.getDisplayName());
        model.setStatus(tblApiKey.getStatus());
        model.setExpiresAt(tblApiKey.getExpiresAt());
        model.setCreatedAt(tblApiKey.getCreatedAt());
        model.setUpdatedAt(tblApiKey.getUpdatedAt());

        return model;
    }

    /**
     * API Key脱敏处理
     * 显示前6位和后4位，中间用*代替
     * 例如：clk_abc123def45678901234 → clk_abc123def4****
     */
    private String maskApiKey(String apiKey) {
        return apiKey.substring(0, 6) + "*******************************" + apiKey.substring(20);
    }

    /**
     * 生成API Key
     */
    private String generateApiKey() {
        return "clk_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }

}
