package com.zcunsoft.clklog.analysis.function;

import com.zcunsoft.clklog.analysis.bean.LogBean;
import com.zcunsoft.clklog.analysis.utils.ExtractUtil;
import org.apache.flink.api.common.functions.RichMapFunction;

import java.util.List;

public class LogRichMapper extends RichMapFunction<String, List<LogBean>> {

    @Override
    public List<LogBean> map(String line) {
        return ExtractUtil.extractToLogBean(line);
    }

}
