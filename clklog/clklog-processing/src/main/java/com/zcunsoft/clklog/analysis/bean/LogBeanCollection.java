package com.zcunsoft.clklog.analysis.bean;

import com.zcunsoft.clklog.core.bean.LogBean;
import lombok.Data;

import java.util.List;

@Data
public class LogBeanCollection {
    private List<LogBean> data;
}
