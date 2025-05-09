package com.zcunsoft.clklog.sysmgmt.repository;

import com.zcunsoft.clklog.sysmgmt.domains.OperRecords;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 操作记录的数据访问仓库
 */
public interface IOperRecordRepository
        extends PagingAndSortingRepository<OperRecords, Integer>, JpaSpecificationExecutor<OperRecords> {

}
