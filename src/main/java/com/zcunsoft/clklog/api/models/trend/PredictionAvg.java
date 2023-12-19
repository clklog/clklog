package com.zcunsoft.clklog.api.models.trend;

import lombok.Data;

@Data
public class PredictionAvg {

    private Float avgPvBefore;

    private Float avgUvBefore;

    private Float avgIpCountBefore;

    private Float avgVisitCountBefore;

    private Float avgPvAfter;

    private Float avgUvAfter;

    private Float avgIpCountAfter;

    private Float avgVisitCountAfter;
}
