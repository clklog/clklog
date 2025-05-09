package com.zcunsoft.clklog.sysmgmt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 操作记录视图类
 */
@Schema(description = "操作记录视图类")
@Data
public class OperRecordDTO {
    @Schema(description = "ID", example = "72754fa3-040a-4177-8edb-2a2df81b7847")
    private int id;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间", example = "2024-05-21 10:57:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp opertime;

    /**
     * 操作用户
     */
    @Schema(description = "操作用户", example = "admin")
    private String user;

    /**
     * 操作
     */
    @Schema(description = "操作", example = "登录")
    private String action;

}
