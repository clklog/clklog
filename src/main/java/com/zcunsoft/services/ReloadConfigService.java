package com.zcunsoft.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 定时加载缓存.
 */
@RequiredArgsConstructor
@Component
public class ReloadConfigService {

    /**
     * 加载间隔.
     */
    private final int loopSpan = 30000;

    /**
     * 接收服务.
     */
    private final IReceiveService ireceiveService;

    /**
     * 加载城市信息.
     */
    @Scheduled(fixedDelay = loopSpan)
    public void loadCity() {
        ireceiveService.loadCity();
    }

    /**
     * 加载项目配置信息.
     */
    @Scheduled(fixedDelay = loopSpan)
    public void loadProjectSetting() {
        ireceiveService.loadProjectSetting();
    }

}
