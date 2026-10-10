package com.zcunsoft.clklog.manage.entity.mysql;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * API密钥实体类
 */
@Entity(name = "tbl_api_key")
@Data
public class TblApiKey {

    /**
     * 主键ID.
     */
    @Id
    @Column(length = 36)
    private String id;

    /**
     * 所属用户ID.
     */
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    /**
     * 所属用户名.
     */
    @Column(name = "username", length = 50, nullable = false)
    private String username;

    /**
     * API密钥.
     */
    @Column(name = "api_key", length = 64, nullable = false, unique = true)
    private String apiKey;

    /**
     * 密钥显示名称.
     */
    @Column(name = "display_name", length = 100, nullable = false)
    private String displayName;

    /**
     * 状态: enabled/disabled.
     */
    @Column(length = 16, nullable = false)
    private String status;

    /**
     * 过期时间.
     */
    @Column(name = "expires_at")
    private Timestamp expiresAt;

    /**
     * 创建时间.
     */
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    /**
     * 更新时间.
     */
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}
