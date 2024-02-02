package com.zcunsoft.handlers;

import com.zcunsoft.dto.QueryCriteria;
import lombok.Data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@Data
public class ConstsDataHolder { 


	private final BlockingQueue<QueryCriteria> logQueue = new LinkedBlockingQueue<>();


}
