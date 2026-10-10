package com.zcunsoft.clklog.manage.repository.mysql;

import com.zcunsoft.clklog.manage.entity.mysql.TblProject;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * 项目的数据访问仓库
 */
@Repository
public interface ProjectRepository extends PagingAndSortingRepository<TblProject, String>, JpaSpecificationExecutor<TblProject> {

}
