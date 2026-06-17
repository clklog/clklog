package com.zcunsoft.clklog.manage.models.apikey;

import com.zcunsoft.clklog.manage.models.R;
import lombok.Data;

/**
 * 删除API密钥响应
 */
@Data
public class DeleteApiKeyResponse extends R<Void> {

    public DeleteApiKeyResponse() {
        super();
    }

    public DeleteApiKeyResponse(int code, String msg) {
        this.setCode(code);
        this.setMsg(msg);
    }
}