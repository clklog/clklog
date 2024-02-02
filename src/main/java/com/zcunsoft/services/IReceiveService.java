package com.zcunsoft.services;

import com.zcunsoft.dto.QueryCriteria;

import java.util.List;

public interface IReceiveService {
    void enqueueKafka(List<QueryCriteria> queryCriteriaList);
}
