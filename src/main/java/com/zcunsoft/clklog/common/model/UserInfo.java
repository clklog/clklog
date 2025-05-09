package com.zcunsoft.clklog.common.model;


import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 用户信息.
 */
@Data
public class UserInfo implements Serializable {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 显示名
     */
    private String displayName;

    /**
     * 创建时间
     */
    private Timestamp createtime;

    /**
     * 创建者
     */
    private String createuser;

    /**
     * 修改时间
     */
    private Timestamp modifytime;

    /**
     * 修改者
     */
    private String modifyuser;

}
