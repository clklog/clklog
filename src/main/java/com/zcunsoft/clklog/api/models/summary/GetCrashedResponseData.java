package com.zcunsoft.clklog.api.models.summary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zcunsoft.clklog.api.entity.clickhouse.CrashedDetailBydate;
import com.zcunsoft.clklog.api.utils.Formatters;
import com.zcunsoft.clklog.api.utils.MathUtils;
import com.zcunsoft.clklog.api.utils.RoundFloatSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Schema(description = "获取崩溃的响应")
@Data
@NoArgsConstructor
public class GetCrashedResponseData {

    @Schema(description = "版本")
    private String appVersion;

    @Schema(description = "型号")
    private String model;

    @Schema(description = "设备型号数")
    private Long modelCount = 0L;

    @Schema(description = "访问量")
    private Long visitCount = 0L;

    @Schema(description = "崩溃次数")
    private Long crashedCount = 0L;

    @Schema(description = "访客数")
    private Long uv = 0L;

    @Schema(description = "崩溃用户数")
    private Long crashedUv = 0L;

    /**
     * 渠道
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "渠道")
    private String channel;

//	@Schema(description = "日期")
//	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
//	private Timestamp statDate;

    /**
     * 统计日期
     */
    @Schema(description = "统计日期")
    private String statTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = RoundFloatSerializer.class)
    @Schema(description = "崩溃数占比")
    public Float getPvRate() {
        return MathUtils.computeRateDefaultOne(visitCount, crashedCount);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = RoundFloatSerializer.class)
    @Schema(description = "崩溃用户占比")
    public Float getUvRate() {
        return MathUtils.computeRateDefaultOne(uv, crashedUv);
    }

//	private List<GetCrashedResponseData> cube = null;

    public GetCrashedResponseData(CrashedDetailBydate domain) {
        this.visitCount = domain.getVisitCount();
        this.crashedCount = domain.getCrashedCount();
        this.uv = domain.getUv();
        this.crashedUv = domain.getCrashedUv();
        this.channel = domain.getLib();
        this.appVersion = domain.getAppVersion();
        this.statTime = domain.getDimension();
        this.model = domain.getModel();
        this.modelCount = domain.getModelCount();
        if (Objects.nonNull(domain.getStatDate())) {
            this.statTime = Formatters.toDateString(domain.getStatDate());
        }
    }

    public static GetCrashedResponseData createDefault(String statTime) {
        GetCrashedResponseData ret = new GetCrashedResponseData();
        ret.setStatTime(statTime);
        return ret;
    }

}
