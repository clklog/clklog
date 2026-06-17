package com.zcunsoft.clklog.manage.models.apikey;

import com.zcunsoft.clklog.manage.models.R;
import lombok.Data;

/**
 * 编辑API密钥响应
 */
@Data
public class EditApiKeyResponse extends R<Void> {

    public EditApiKeyResponse() {
        super();
    }

    public EditApiKeyResponse(int code, String msg) {
        this.setCode(code);
        this.setMsg(msg);
    }
}