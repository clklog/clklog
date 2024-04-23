package com.zcunsoft.clklog.api.models.profile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "客户信息")
@Data
public class UpdateProfileRequest {

//	@Schema(description = "是否订阅", requiredMode = Schema.RequiredMode.REQUIRED, example = "true,false")
//	@NotNull(message = "是否订阅不能为空")
//	private Boolean subscribed;

	/**
	 * 联系人
	 */
	@Schema(description = "联系人", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "张三")
	private String contact;

	/**
	 * 联系电话
	 */
	@Schema(description = "联系电话", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "13111111111")
	private String phone;

	/**
	 * 邮箱地址
	 */
	@Schema(description = "邮箱", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "abc@def.com")
	@Email
	private String email;

	/**
	 * 组织名称
	 */
	@Schema(description = "组织名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String orgnizationName;

	/**
	 * 项目名称
	 */
	@Schema(description = "项目名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String projectName;

	/**
	 * 项目类型
	 */
	@Schema(description = "项目类型", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String projectType;

	/**
	 * 项目说明
	 */
	@Schema(description = "项目说明", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String remark;

	/**
	 * 接收通知
	 */
	@Schema(description = "接收通知", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private Boolean receiveNotification;

}
