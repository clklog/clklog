package com.zcunsoft.clklog.api.models.visituri;

import lombok.Data;

import java.util.List;

@Data
public class UriPathNode {

    private String uriPath;

    private String parentUriPath;

    private String host;

    private String segment;

    private String splitChar;

    private boolean isStartSplitChar;

    private List<UriPathNode> leaves;

    private List<String> originalUriPath;
}
