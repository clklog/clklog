package com.zcunsoft.clklog.analysis.utils;


import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.MapperFeature;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;


public class ObjectMapperUtil extends ObjectMapper {
	private static final long serialVersionUID = 1L;

	public ObjectMapperUtil() {
		super();
		configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
}
