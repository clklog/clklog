package com.zcunsoft.services;


import com.zcunsoft.cfg.InitSetting;
import com.zcunsoft.handlers.ConstsDataHolder;
import com.zcunsoft.util.IOUtil;
import com.zcunsoft.util.ObjectMapperUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


@Service
public class InitServiceImpl implements IInitService {

    private final ConstsDataHolder constsDataHolder;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapperUtil objectMapper;


    private final JdbcTemplate clickHouseJdbcTemplate;


    private final InitSetting setting;

    private final ThreadLocal<DateFormat> yMdFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };


    public InitServiceImpl(ConstsDataHolder constsDataHolder, ObjectMapperUtil objectMapper, JdbcTemplate clickHouseJdbcTemplate, InitSetting setting) {
        this.objectMapper = objectMapper;
        this.constsDataHolder = constsDataHolder;
        this.clickHouseJdbcTemplate = clickHouseJdbcTemplate;
        this.setting = setting;
    }

    @Override
    public void calScript(String script_name) {
        try {
            String tableName = script_name.replaceAll("-", "_");
            String sql = IOUtil.readFile(getResourcePath() + File.separator + "scripts" + File.separator
                    + tableName + ".sql");
            sql = sql.replace("${CLKLOG_LOG_DB}", setting.getLogDb());
            if (!sql.isEmpty()) {
                sql = sql.replaceAll(":cal_date", yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis())));
                sql = sql.replaceAll(":previous_date", yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis() - 86400000 * 7)));
                if (script_name.equalsIgnoreCase("visitor_life_bydate")) {
                    sql = sql.replaceAll(":before_date_1", yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis() - 86400000)));
                    sql = sql.replaceAll(":before_date_2", yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis() - 86400000 * 2)));
                    sql = sql.replaceAll(":before_date_3", yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis() - 86400000 * 3)));
                }
                logger.info(sql);
                clickHouseJdbcTemplate.execute(sql);
                clickHouseJdbcTemplate.execute("optimize table clklog." + tableName + " FINAL SETTINGS optimize_skip_merged_partitions=1");
            }
        } catch (Exception ex) {
            logger.error("calScript " + script_name + " error ", ex);
        }
    }

    @Override
    public boolean initDb() {
        boolean isOk = false;

        try {

            String sql = FileUtils.readFileToString(new File(getResourcePath() + File.separator + "scripts" + File.separator
                    + "init.sql"), Charset.forName("GB2312"));
            sql = sql.replace("${CLKLOG_LOG_DB}", setting.getLogDb());
            clickHouseJdbcTemplate.execute(sql);
            isOk = true;
        } catch (Exception ex) {
            logger.error("initDb error ", ex);
        }

        return isOk;
    }

    private String getResourcePath() {
        if (setting.getResourcePath() == null || setting.getResourcePath().trim().isEmpty()) {
            return System.getProperty("user.dir");
        } else {
            return setting.getResourcePath();
        }
    }
}
