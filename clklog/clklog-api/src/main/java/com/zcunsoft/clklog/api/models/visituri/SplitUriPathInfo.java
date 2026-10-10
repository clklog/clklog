package com.zcunsoft.clklog.api.models.visituri;

import lombok.Data;

@Data
public class SplitUriPathInfo {
    private String[] uriPathArr;

    private String splitChar;
}
