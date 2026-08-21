package com.zcunsoft.clklog.sysmgmt.repository;

import com.zcunsoft.clklog.sysmgmt.domains.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 用户的数据访问仓库
 */
public interface IUserRepository extends PagingAndSortingRepository<User, String>, JpaSpecificationExecutor<User> {

}
