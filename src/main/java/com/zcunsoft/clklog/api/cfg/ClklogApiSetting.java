package com.zcunsoft.clklog.api.cfg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("clklogapi")
public class ClklogApiSetting {

    @Value("#{'${clklogapi.access-control-allow-origin}'.split(',')}")
    private List<String> accessControlAllowOrigin;

    @Value("${clklogapi.project-name:hqq}")
    private String projectName;

    public List<String> getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    public void setAccessControlAllowOrigin(List<String> accessControlAllowOrigin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
