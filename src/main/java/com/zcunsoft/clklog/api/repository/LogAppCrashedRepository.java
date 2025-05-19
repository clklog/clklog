package com.zcunsoft.clklog.api.repository;

import com.zcunsoft.clklog.api.entity.ck.LogAppCrashed;
import org.springframework.stereotype.Repository;

/**
 * App崩溃的数据访问仓库
 */
@Repository
public interface LogAppCrashedRepository extends BaseRepository<LogAppCrashed, String> {

}
