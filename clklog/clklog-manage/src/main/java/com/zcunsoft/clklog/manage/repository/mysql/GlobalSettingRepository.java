package com.zcunsoft.clklog.manage.repository.mysql;

import com.zcunsoft.clklog.manage.entity.mysql.TblGlobalSetting;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * 全局项目的数据访问仓库
 */
@Repository
public interface GlobalSettingRepository extends PagingAndSortingRepository<TblGlobalSetting, String>, JpaSpecificationExecutor<TblGlobalSetting> {

}
