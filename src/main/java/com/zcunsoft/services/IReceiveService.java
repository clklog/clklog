package com.zcunsoft.services;

import com.zcunsoft.model.LogBean;
import com.zcunsoft.model.QueryCriteria;
import com.zcunsoft.model.Region;

import java.util.List;

public interface IReceiveService {
    void enqueueKafka(List<QueryCriteria> queryCriteriaList);
    
    Region analysisRegionFromIp(String clientIp);

    List<LogBean> analysisData(QueryCriteria queryCriteria);

    void saveToClickHouse(List<QueryCriteria> queryCriteriaList);

    void loadCity();

    void loadProvince();

    void loadCountry();

    void loadAppSetting();
}
