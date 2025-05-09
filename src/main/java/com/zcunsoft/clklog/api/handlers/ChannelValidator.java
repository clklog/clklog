package com.zcunsoft.clklog.api.handlers;

import com.zcunsoft.clklog.api.services.utils.FilterBuildUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 渠道校验器
 */
public class ChannelValidator implements ConstraintValidator<IChannel, List<String>> {

    @Override
    public boolean isValid(List<String> valueList, ConstraintValidatorContext context) {
        boolean valid = true;
        if (valueList != null) {
            LinkedHashMap<String, String> linkedHashMap = FilterBuildUtils.getLibTypeMap();
            for (String value : valueList) {
                boolean singleValid = false;
                if (value != null) {
                    if (value.trim().isEmpty()) {
                        singleValid = true;
                    } else {
                        for (Map.Entry<String, String> item : linkedHashMap.entrySet()) {
                            if (item.getValue().split(",")[1].equals(value)) {
                                singleValid = true;
                                break;
                            }
                        }
                    }
                }
                valid = singleValid;
                if (!singleValid) {
                    break;
                }
            }
        }
        return valid;
    }
}
