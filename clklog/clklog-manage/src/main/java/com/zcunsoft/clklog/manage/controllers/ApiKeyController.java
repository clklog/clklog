package com.zcunsoft.clklog.manage.controllers;

import com.zcunsoft.clklog.manage.models.apikey.*;
import com.zcunsoft.clklog.manage.services.ApiKeyService;
import com.zcunsoft.clklog.manage.services.IManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * API密钥管理
 */
@RestController
@RequestMapping(path = "apikey")
@Tag(name = "API密钥管理", description = "API密钥管理")
public class ApiKeyController {

    @Resource
    ApiKeyService apiKeyService;

    @Operation(summary = "获取API密钥详情")
    @RequestMapping(path = "/get", method = RequestMethod.POST)
    public GetApiKeyResponse get(@RequestBody GetApiKeyRequest request) {
        return apiKeyService.getApiKey(request);
    }

    @Operation(summary = "添加API密钥")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public AddApiKeyResponse add(@Valid @RequestBody AddApiKeyRequest request) {
        return apiKeyService.addApiKey(request);
    }

    @Operation(summary = "编辑API密钥")
    @RequestMapping(path = "/edit", method = RequestMethod.POST)
    public EditApiKeyResponse edit(@Valid @RequestBody EditApiKeyRequest request) {
        return apiKeyService.editApiKey(request);
    }

    @Operation(summary = "删除API密钥")
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public DeleteApiKeyResponse delete(@Valid @RequestBody DeleteApiKeyRequest request) {
        return apiKeyService.deleteApiKey(request);
    }

    @Operation(summary = "获取API密钥列表")
    @RequestMapping(path = "/list", method = RequestMethod.POST)
    public ListApiKeyResponse list(@RequestBody ListApiKeyRequest request) {
        return apiKeyService.listApiKey(request);
    }
}
