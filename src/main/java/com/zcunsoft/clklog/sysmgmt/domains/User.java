package com.zcunsoft.clklog.sysmgmt.domains;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 用户实体表.
 */
@Entity
@Table(name = "sys_user")
@Data
public class User implements Serializable {

    /**
     * 用户ID.
     */
    @Id
    @Column(length = 36)
    private String userId;

    /**
     * 用户名.
     */
    @NotEmpty(message = "用户名不能为空")
    private String userName;

    /**
     * 密码.
     */
    @NotEmpty(message = "密码不能为空")
    private String password;

    /**
     * 昵称.
     */
    @NotEmpty(message = "昵称不能为空")
    private String displayName;

    /**
     * 创建时间.
     */
    @Column(columnDefinition = "DATETIME(3) NOT NULL")
    private Timestamp createtime;

    /**
     * 创建者.
     */
    private String createuser;

    /**
     * 修改时间.
     */
    @Column(columnDefinition = "DATETIME(3)")
    private Timestamp modifytime;

    /**
     * 修改人.
     */
    private String modifyuser;

    /**
     * 上次登录时间.
     */
    @Column(columnDefinition = "DATETIME(3)")
    private Timestamp lastlogintime;
}
