package com.zcunsoft.clklog.manage.repository.mysql;

import com.zcunsoft.clklog.manage.entity.mysql.TblProjectStat;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * 项目统计的数据访问仓库
 */
@Repository
public interface ProjectStatRepository extends PagingAndSortingRepository<TblProjectStat, String>, JpaSpecificationExecutor<TblProjectStat> {

    /**
     * 获取统计的项目数
     *
     * @return {@link Integer }
     */
    @Query(value = "select count(distinct(project_name)) as projectCount from tbl_project_stat", nativeQuery = true)
    Integer getStatProjectCount();
}
