package com.zcunsoft.handlers;

import com.zcunsoft.model.ProjectSetting;
import com.zcunsoft.model.QueryCriteria;
import lombok.Data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;


@Data
public class ConstsDataHolder {

	private final ConcurrentMap<String, String> htForCity = new ConcurrentHashMap<String, String>();

	private final BlockingQueue<QueryCriteria> logQueue = new LinkedBlockingQueue<>();


	private final ConcurrentMap<String, ProjectSetting> htProjectSetting = new ConcurrentHashMap<String, ProjectSetting>();
}
