package com.zcunsoft.clklog.manage.models.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;


public interface ProjectStatInterface {
    @Schema(description = "项目编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    String getProjectName();

    @Schema(description = "日志数量", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    Long getLogRecordCount();

    @Schema(description = "日志大小", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    Long getLogSpaceSize();

    @Schema(description = "日志天数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    Integer getLogDays();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    @Schema(description = "最新日志时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    Timestamp getLogLatestTime();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    @Schema(description = "数据库表第一条记录时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    Timestamp getDbFirstTime();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    @Schema(description = "数据库表最新记录时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    Timestamp getDbLatestTime();

    @Schema(description = "数据库表记录数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    Long getDbRecordCount();

    @Schema(description = "数据库表占用空间大小", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    Long getDbSpaceSize();
}
