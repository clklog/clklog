package com.zcunsoft.clklog.manage.models.apikey;

import com.zcunsoft.clklog.manage.models.R;
import lombok.Data;

/**
 * 添加API密钥响应
 */
@Data
public class AddApiKeyResponse extends R<AddApiKeyResponseData> {

    public AddApiKeyResponse() {
        super();
    }

    public AddApiKeyResponse(int code, String msg) {
        this.setCode(code);
        this.setMsg(msg);
    }
}