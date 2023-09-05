package com.zcunsoft.clklog.analysis.utils

import ru.yandex.clickhouse.BalancedClickhouseDataSource
import ru.yandex.clickhouse.settings.ClickHouseProperties

import java.sql.Connection

object ClickHouseUtil {

  var conn: Connection = _

  def getConn(ip_ports: String, database: String, userName: String, password: String): Connection = {
    try {
      if (conn == null) {
        val url = if (database.nonEmpty) {
          s"jdbc:clickhouse://$ip_ports/$database"
        } else s"jdbc:clickhouse://$ip_ports/default"

        val ckProperties = new ClickHouseProperties()
        ckProperties.setUser(userName)
        ckProperties.setPassword(password)

        val balanced = new BalancedClickhouseDataSource(url, ckProperties)
        //对每个host进行ping操作, 排除不可用的连接//对每个host进行ping操作, 排除不可用的连接
        balanced.actualize
        conn = balanced.getConnection
        println("clickhouse connection success...")
      }
    } catch {
      case e: Throwable =>
        throw new Exception("clickhouse connection fail,result : " + e.toString)
    }
    conn
  }

  def close() = {
    if (conn != null) conn.close()
  }


}
