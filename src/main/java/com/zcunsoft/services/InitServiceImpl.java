package com.zcunsoft.services;


import com.zcunsoft.cfg.InitSetting;
import com.zcunsoft.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;


@Service
public class InitServiceImpl implements IInitService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 仅允许字母、数字、下划线，长度 1~64，防止路径遍历与 SQL 注入
     */
    private static final Pattern SCRIPT_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{1,64}$");



    private final JdbcTemplate clickHouseJdbcTemplate;


    private final InitSetting setting;

    private final ThreadLocal<DateFormat> yMdFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };


    public InitServiceImpl(JdbcTemplate clickHouseJdbcTemplate, InitSetting setting) {
        this.clickHouseJdbcTemplate = clickHouseJdbcTemplate;
        this.setting = setting;
    }

    @Override
    public void calScript(String script_name) {
        try {
            // 入口白名单校验，杜绝 SQL 注入与路径遍历
            validateScriptName(script_name);

            String tableName = script_name.replaceAll("-", "_");
            // 再次校验转换后的 tableName（防御性）
            validateScriptName(tableName);

            // 规范化路径，确保最终文件必须位于 scripts 目录内
            Path baseDir = Paths.get(getResourcePath(), "scripts").normalize();
            Path target = baseDir.resolve(tableName + ".sql").normalize();
            if (!target.startsWith(baseDir)) {
                throw new SecurityException("path traversal detected: " + target);
            }
            String sql = IOUtil.readFile(target.toString());
            sql = sql.replace("${CLKLOG_LOG_DB}", setting.getLogDb());
            if (!sql.isEmpty()) {
                long now = System.currentTimeMillis();
                String statDate = yMdFORMAT.get().format(new Timestamp(now));
                sql = sql.replaceAll(":cal_date", statDate);
                sql = sql.replaceAll(":previous_date", yMdFORMAT.get().format(new Timestamp(now - 86400000L * (setting.getEventSessionAcrossDay() - 1))));
                if (script_name.equalsIgnoreCase("visitor_life_bydate")) {
                    sql = sql.replaceAll(":before_date_1", yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis() - 86400000)));
                    sql = sql.replaceAll(":before_date_2", yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis() - 86400000 * 2)));
                    sql = sql.replaceAll(":before_date_3", yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis() - 86400000 * 3)));
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(sql);
                }
                clickHouseJdbcTemplate.execute(sql);
                // tableName 已通过白名单校验，可安全拼接
                clickHouseJdbcTemplate.execute("optimize table " + setting.getLogDb() + "." + tableName + " FINAL SETTINGS optimize_skip_merged_partitions=1");
            }
        } catch (Exception ex) {
            logger.error("calScript " + script_name + " error ", ex);
        }
    }

    @Override
    public boolean initDb() {
        boolean isOk = false;

        try {
            // 校验 logDb，防止配置被污染导致 SQL 注入
            validateScriptName(setting.getLogDb());

            String sql = IOUtil.readFile(getResourcePath() + File.separator + "scripts" + File.separator
                    + "init.sql");
            sql = sql.replace("${CLKLOG_LOG_DB}", setting.getLogDb());
            clickHouseJdbcTemplate.execute(sql);
            isOk = true;
        } catch (Exception ex) {
            logger.error("initDb error ", ex);
        }

        return isOk;
    }

    /**
     * 校验标识符（脚本名/表名/库名）：仅允许字母、数字、下划线，长度 1~64。
     * 用于阻断路径遍历与 SQL 标识符注入（表名/库名无法参数化，只能白名单）。
     */
    private void validateScriptName(String name) {
        if (name == null || !SCRIPT_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("invalid identifier: " + name);
        }
    }

    private String getResourcePath() {
        if (setting.getResourcePath() == null || setting.getResourcePath().trim().isEmpty()) {
            return System.getProperty("user.dir");
        } else {
            return setting.getResourcePath();
        }
    }
}
