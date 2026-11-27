package com.zcunsoft.services;

import com.zcunsoft.clklog.core.bean.LogBean;
import com.zcunsoft.clklog.core.bean.Region;
import com.zcunsoft.model.RawMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IReceiveService {
    void enqueueKafka(List<RawMessage> rawMessageList);

    Region analysisRegionFromIp(String clientIp);

    Region analysisRegionFromIpBaseOnIp2Loc(String clientIp);

    List<LogBean> analysisData(RawMessage rawMessage);

    void saveToClickHouse(List<RawMessage> rawMessageList);

    void loadCity();

    void loadProjectSetting();

    String extractLog(RawMessage rawMessage, HttpServletRequest request);
}
