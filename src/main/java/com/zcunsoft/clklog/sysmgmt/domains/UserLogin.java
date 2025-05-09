package com.zcunsoft.clklog.sysmgmt.domains;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * 用户登录实体表.
 */
@Entity
@Table(name = "sys_userlogin")
@Data
public class UserLogin {

    /**
     * 用户凭据.
     */
    @Id
    @Column(length = 200)
    private String token;

    /**
     * 用户名.
     */
    private String userName = "";

    /**
     * 创建时间.
     */
    private Timestamp createTime;

}
