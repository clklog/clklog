package com.zcunsoft.clklog.api.models;

import lombok.Data;

@Data
public class ClientProfile {

	private String clientId;
	
	private Boolean subscribed;
	
	/**
	 * 联系人
	 */
	private String contact;

	/**
	 * 联系电话
	 */
	private String phone;

	/**
	 * 邮箱地址
	 */
	private String email;

	/**
	 * 组织名称
	 */
	private String orgnizationName;

	/**
	 * 项目名称
	 */
	private String projectName;

	/**
	 * 项目类型
	 */
	private String projectType;

	/**
	 * 项目说明
	 */
	private String remark;

	/**
	 * 接收通知
	 */
	private Boolean receiveNotification;
	
}
