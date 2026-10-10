package com.zcunsoft.clklog.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json工具.
 */
public class ObjectMapperUtil extends ObjectMapper {
    private static final long serialVersionUID = 1L;

    public ObjectMapperUtil() {
        super();
        configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
