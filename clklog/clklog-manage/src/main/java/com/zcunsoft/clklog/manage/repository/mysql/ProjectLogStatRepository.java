package com.zcunsoft.clklog.manage.repository.mysql;

import com.zcunsoft.clklog.manage.entity.mysql.TblProjectLogStat;
import com.zcunsoft.clklog.manage.models.project.ProjectMaxStatDate;
import com.zcunsoft.clklog.manage.models.project.ProjectStatInterface;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * 项目每日统计的数据访问仓库
 */
@Repository
public interface ProjectLogStatRepository extends PagingAndSortingRepository<TblProjectLogStat, String>, JpaSpecificationExecutor<TblProjectLogStat> {

    @Query(value = "select project_name as projectName,max(stat_date) as statDate from tbl_project_log_stat group by project_name", nativeQuery = true)
    List<ProjectMaxStatDate> getMaxStatDateGroupByProject();

    @Query(value = "select  project_name as projectName, sum(log_record_count) as logRecordCount,max(log_latest_time) as logLatestTime ,sum(log_space_size) as logSpaceSize ,sum(db_record_count) as dbRecordCount, count(1) as logDays,min(db_first_time) as dbFirstTime,max(db_latest_time) as dbLatestTime from tbl_project_log_stat where (?1 is null or stat_date >= ?1) and (?2 is null or stat_date <= ?2) group by project_name", nativeQuery = true)
    List<ProjectStatInterface> getStatGroupByProject(Timestamp startDate, Timestamp endDate);
}
