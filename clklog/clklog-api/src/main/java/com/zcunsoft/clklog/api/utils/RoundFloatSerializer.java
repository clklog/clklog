package com.zcunsoft.clklog.api.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * 浮点数序列化.
 */
public class RoundFloatSerializer extends JsonSerializer<Float> {

    @Override
    public void serialize(Float value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeNumber(new DecimalFormat("0.####")
                .format(value));
    }
}
