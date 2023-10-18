package com.zcunsoft.clklog.api.models.visituri;

import lombok.Data;

import java.util.List;

@Data
public class VisitUriTreeStatData {

    private String uri;
    private String path;
    private String segment;
    private List<VisitUriTreeStatData> leafUri;
    private VisitUriPathDetail detail;
}
