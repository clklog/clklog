package com.zcunsoft.clklog.manage.repository.mysql;

import com.zcunsoft.clklog.manage.entity.mysql.TblApiKey;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * API密钥的数据访问仓库
 */
@Repository
public interface ApiKeyRepository extends PagingAndSortingRepository<TblApiKey, String>, JpaSpecificationExecutor<TblApiKey> {

}
