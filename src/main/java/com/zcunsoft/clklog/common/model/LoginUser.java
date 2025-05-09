package com.zcunsoft.clklog.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 登录用户身份权限
 */
public class LoginUser implements UserDetails {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 用户唯一标识
	 */
	private String token;

	/**
	 * 登录时间
	 */
	private Long loginTime;

	/**
	 * 过期时间
	 */
	private Long expireTime;

	/**
	 * 登录IP地址
	 */
	private String ipaddr;

	/**
	 * 用户信息
	 */
	private UserInfo user;

	/**
	 * 权限信息
	 */
	private Map<String, Set<String>> perms;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LoginUser() {
	}

	public LoginUser(UserInfo user) {
		this.user = user;
	}

	public LoginUser(String userId, UserInfo user) {
		this.userId = userId;
		this.user = user;
	}

	public LoginUser(String userId, UserInfo user, Map<String, Set<String>> perms) {
		this.userId = userId;
		this.user = user;
		this.perms = perms;
	}

	@JsonIgnore
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	/**
	 * 账户是否未过期,过期无法验证
	 */
	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * 指定用户是否解锁,锁定的用户无法进行身份验证
	 *
	 * @return true
	 */
	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
	 *
	 * @return true
	 */
	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * 是否可用 ,禁用的用户不能身份验证
	 *
	 * @return true
	 */
	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}

	public Long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Long loginTime) {
		this.loginTime = loginTime;
	}

	public String getIpaddr() {
		return ipaddr;
	}

	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	public Map<String, Set<String>> getPerms() {
		return perms;
	}

	public void setPerms(Map<String, Set<String>> perms) {
		this.perms = perms;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}
}
