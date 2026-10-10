package com.zcunsoft.clklog.manage.daemons;

import com.zcunsoft.clklog.manage.services.IManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时缓存信息.
 */
@RequiredArgsConstructor
@Component
public class ReloadConfigService {
    private final int loopSpan = 30000;

    private final IManageService manageService;

    /**
     * 缓存项目配置.
     */
    @Scheduled(fixedDelay = loopSpan)
    public void reloadProjectSetting() {
        manageService.reloadProjectSetting();
    }


    /**
     * 统计项目的日志文件和数据库表.
     */
    @Scheduled(fixedDelay = loopSpan)
    public void statByProjectName() {
        manageService.statByProjectName();
    }

    /**
     * 缓存国家中英文对照表.
     */
    @Scheduled(fixedDelay = 60000)
    public void cacheCountryEngChsMap() {
        manageService.cacheCountryEngChsMap();
    }

    /**
     * 缓存城市中英文对照表.
     */
    @Scheduled(fixedDelay = 60000)
    public void cacheCityEngChsMap() {
        manageService.cacheCityEngChsMap();
    }

}
