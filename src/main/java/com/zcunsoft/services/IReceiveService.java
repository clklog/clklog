package com.zcunsoft.services;

import com.zcunsoft.model.LogBean;
import com.zcunsoft.model.QueryCriteria;
import com.zcunsoft.model.Region;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IReceiveService {
    void enqueueKafka(List<QueryCriteria> queryCriteriaList);

    Region analysisRegionFromIp(String clientIp);

    Region analysisRegionFromIpBaseOnIp2Loc(String clientIp);

    List<LogBean> analysisData(QueryCriteria queryCriteria);

    void saveToClickHouse(List<QueryCriteria> queryCriteriaList);

    void loadCity();

    void loadProjectSetting();

    void extractLog(QueryCriteria queryCriteria, HttpServletRequest request);
}
