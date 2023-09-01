package com.zcunsoft.services;

import com.zcunsoft.dto.QueryCriteria;
import com.zcunsoft.dto.Region;

import java.util.List;

public interface IReceiveService {
    Region analysisRegionFromIp(String clientIp);

    String analysisData(QueryCriteria queryCriteria);

    void enqueueKafka(List<QueryCriteria> queryCriteriaList);
}
