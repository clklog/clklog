package com.zcunsoft.clklog.api.models.crash;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zcunsoft.clklog.api.handlers.IChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页获取崩溃日志的请求
 */
@Schema(description = "分页获取崩溃日志的请求")
@Data
public class AppCrashedPageRequest {

    /**
     * 页码
     */
    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int pageNum;

    /**
     * 页长
     */
    @Schema(description = "页长", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private int pageSize;

    /**
     * 渠道
     */
    @Schema(description = "渠道", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"安卓\",\"苹果\",\"网站\",\"微信小程序\"]")
    @IChannel
    private List<String> channel = new ArrayList<>();

    @Schema(description = "应用版本", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1.0")
    private String version;
    /**
     * 开始时间
     */
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-08")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-10")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp endTime;

    /**
     * 项目编码
     */
    @Schema(description = "项目编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    @NotEmpty(message = "项目编码不能为空")
    private String projectName;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "HONGMI9")
    private String model;

}
