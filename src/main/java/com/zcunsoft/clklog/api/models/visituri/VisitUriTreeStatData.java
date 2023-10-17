package com.zcunsoft.clklog.api.models.visituri;

import lombok.Data;

import java.util.List;

@Data
public class VisitUriTreeStatData {

    private String uri;
    private List<VisitUriTreeStatData> leafUri;

    private VisitUriDetail detail;
}
