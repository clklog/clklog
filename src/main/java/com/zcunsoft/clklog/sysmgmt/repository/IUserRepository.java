package com.zcunsoft.clklog.sysmgmt.repository;

import com.zcunsoft.clklog.sysmgmt.domains.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 用户的数据访问仓库
 */
public interface IUserRepository extends PagingAndSortingRepository<User, String>, JpaSpecificationExecutor<User> {

    /**
     * 按用户名查询用户
     *
     * @param userName 用户名
     * @return 用户实体，不存在时返回 null
     */
    User getByUserName(String userName);
}
