package com.zcunsoft.clklog.manage.models.apikey;

import com.zcunsoft.clklog.manage.models.R;
import lombok.Data;

/**
 * 获取API密钥详情响应
 */
@Data
public class GetApiKeyResponse extends R<ApiKeyModel> {

    public GetApiKeyResponse() {
        super();
    }

    public GetApiKeyResponse(int code, String msg) {
        this.setCode(code);
        this.setMsg(msg);
    }
}