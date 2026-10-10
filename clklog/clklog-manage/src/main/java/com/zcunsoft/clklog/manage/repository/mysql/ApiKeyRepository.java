package com.zcunsoft.clklog.manage.repository.mysql;

import com.zcunsoft.clklog.manage.entity.mysql.TblApiKey;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * API密钥的数据访问仓库
 */
@Repository
public interface ApiKeyRepository extends PagingAndSortingRepository<TblApiKey, String>, JpaSpecificationExecutor<TblApiKey> {

    /**
     * 按ID与所属用户ID联合查询API密钥
     * 在数据访问层直接限定数据归属，防止越权访问他人密钥
     *
     * @param id     API密钥ID
     * @param userId 所属用户ID
     * @return API密钥
     */
    Optional<TblApiKey> findByIdAndUserId(String id, String userId);
}
