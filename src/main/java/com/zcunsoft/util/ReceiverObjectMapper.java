package com.zcunsoft.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReceiverObjectMapper extends ObjectMapper {
	private static final long serialVersionUID = 1L;

	public ReceiverObjectMapper() {
		super();
		configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
}
