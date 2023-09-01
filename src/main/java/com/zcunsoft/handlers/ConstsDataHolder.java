package com.zcunsoft.handlers;

import com.zcunsoft.dto.QueryCriteria;
import lombok.Data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;


@Data
public class ConstsDataHolder { 

	private final ConcurrentMap<String, String> htForCountry = new ConcurrentHashMap<String, String>();

	private final ConcurrentMap<String, String> htForProvince = new ConcurrentHashMap<String, String>();


	private final ConcurrentMap<String, String> htForCity = new ConcurrentHashMap<String, String>();

	private final BlockingQueue<QueryCriteria> logQueue = new LinkedBlockingQueue<>();



}
