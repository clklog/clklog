package com.zcunsoft.clklog.analysis.function;

import com.zcunsoft.clklog.analysis.bean.ProjectSetting;
import com.zcunsoft.clklog.analysis.bean.LogBean;
import com.zcunsoft.clklog.analysis.bean.LogBeanCollection;
import com.zcunsoft.clklog.analysis.bean.Region;
import com.zcunsoft.clklog.analysis.utils.ExtractUtil;
import com.zcunsoft.clklog.analysis.utils.IPUtil;
import com.zcunsoft.clklog.analysis.utils.ObjectMapperUtil;
import nl.basjes.parse.useragent.AbstractUserAgentAnalyzer;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LogRichMapper extends RichMapFunction<String, LogBeanCollection> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AbstractUserAgentAnalyzer userAgentAnalyzer;

    private transient Jedis jedis;

    private IPUtil ipUtil;

    private HashMap<String, ProjectSetting> htProjectSetting;

    @Override
    public LogBeanCollection map(String line) {
        List<LogBean> logBeanList = ExtractUtil.extractToLogBean(line, userAgentAnalyzer, htProjectSetting);
        if (!logBeanList.isEmpty()) {
            List<String> clientIpList = logBeanList.stream().map(LogBean::getClientIp).distinct().collect(Collectors.toList());
            List<String> ipInfoList = jedis.hmget("ClientIpRegionHash", clientIpList.toArray(new String[0]));
            for (LogBean logBean : logBeanList) {
                if (StringUtils.isNotBlank(logBean.getClientIp())) {
                    Region region = null;
                    int index = clientIpList.indexOf(logBean.getClientIp());
                    String regionInfo = ipInfoList.get(index);
                    if (StringUtils.isNotBlank(regionInfo)) {
                        region = new Region();
                        String[] regionArr = regionInfo.split(",", -1);
                        if (regionArr.length == 4) {
                            region.setCountry(regionArr[1]);
                            region.setProvince(regionArr[2]);
                            region.setCity(regionArr[3]);
                        }
                    } else {
                        region = ipUtil.analysisRegionFromIp(logBean.getClientIp());
                        String sbRegion = region.getClientIp() + "," + region.getCountry() + "," + region.getProvince() + "," + region.getCity();
                        jedis.hset("ClientIpRegionHash", region.getClientIp(), sbRegion);
                    }
                    logBean.setCountry(region.getCountry());
                    logBean.setProvince(region.getProvince());
                    logBean.setCity(region.getCity());
                }
            }
        }
        LogBeanCollection logBeanCollection = new LogBeanCollection();
        logBeanCollection.setData(logBeanList);
        return logBeanCollection;
    }


    @Override
    public void open(Configuration configuration) throws IOException {
        ParameterTool parameters = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();

        String redisHost = parameters.get("redis.host");
        int redisPort = parameters.getInt("redis.port");
        String redisPwd = parameters.get("redis.password");
        if ("".equalsIgnoreCase(redisPwd)) {
            redisPwd = null;
        }

        jedis = new Jedis(redisHost, redisPort);
        if (StringUtils.isNotBlank(redisPwd)) {
            jedis.auth(redisPwd);
        }
        logger.info("open redis ok");
        ipUtil = new IPUtil(parameters.get("processing-file-location"));
        ipUtil.loadIpFile();

        userAgentAnalyzer = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(10000)
                .build();


        String projectSettingContent = FileUtils.readFileToString(new File(parameters.get("processing-file-location") + File.separator + "project-setting.json"), Charset.forName("GB2312"));
        TypeReference<HashMap<String, ProjectSetting>> htProjectSettingTypeReference = new TypeReference<HashMap<String, ProjectSetting>>() {
        };
        ObjectMapperUtil mapper = new ObjectMapperUtil();
        htProjectSetting = mapper.readValue(projectSettingContent, htProjectSettingTypeReference);
    }
}
