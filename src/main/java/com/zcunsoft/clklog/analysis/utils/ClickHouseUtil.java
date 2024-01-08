package com.zcunsoft.clklog.analysis.utils;

import com.clickhouse.jdbc.ClickHouseDriver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class ClickHouseUtil {
  private static Connection connection;

  private static final Logger logger = LoggerFactory.getLogger(ClickHouseUtil.class);

  public static Connection getConn(String host, String database, String userName, String password) {
    try {

      String url = "jdbc:clickhouse://" + userName;
      if (StringUtils.isNotBlank(password)) {
        url += ":" + password;
      }
      url += "@" + host;

      if (StringUtils.isNotBlank(database)) {
        url += "/" + database;
      }
      Driver driver = new ClickHouseDriver();
      connection = driver.connect(url, new Properties());
      logger.info("ck conn got");
    } catch (Exception ex) {
      logger.error("ck conn error ", ex);
    }
    return connection;
  }

  public void close() throws SQLException {
    connection.close();
  }
}

