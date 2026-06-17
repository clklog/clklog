package com.zcunsoft.clklog.manage.models.apikey;

import com.zcunsoft.clklog.manage.models.R;
import lombok.Data;

import java.util.List;

/**
 * 获取API密钥列表响应
 */
@Data
public class ListApiKeyResponse extends R<List<ApiKeyModel>> {

    public ListApiKeyResponse() {
        super();
    }

    public ListApiKeyResponse(int code, String msg) {
        this.setCode(code);
        this.setMsg(msg);
    }
}