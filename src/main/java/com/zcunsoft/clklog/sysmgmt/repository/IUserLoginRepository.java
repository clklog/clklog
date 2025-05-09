package com.zcunsoft.clklog.sysmgmt.repository;

import com.zcunsoft.clklog.sysmgmt.domains.UserLogin;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 用户登录的数据访问仓库
 */
public interface IUserLoginRepository
        extends PagingAndSortingRepository<UserLogin, String>, JpaSpecificationExecutor<UserLogin> {

}
