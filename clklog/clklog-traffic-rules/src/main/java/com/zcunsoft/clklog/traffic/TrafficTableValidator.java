package com.zcunsoft.clklog.traffic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 在写入链路启动时检查普通日志表与机器人表的结构兼容性。
 *
 * <p>普通表中的每一列都必须在机器人表中存在且 JDBC 类型名称一致，
 * 机器人表还必须包含识别结果列。校验失败时抛出异常，避免运行后
 * 因目标表缺失或基础字段不兼容而持续写入失败。</p>
 */
public final class TrafficTableValidator {
    private TrafficTableValidator() { }

    /**
     * 读取两表元数据并校验；只检查识别结果列是否存在，不比较这些新增列的类型。
     *
     * @param connection 已连接到目标日志数据库的 JDBC 连接
     * @throws SQLException 查询失败或表结构不满足写入要求
     */
    public static void validate(Connection connection) throws SQLException {
        Map<String, String> normal = columns(connection, "log_analysis");
        Map<String, String> bot = columns(connection, "log_analysis_bot");
        for (Map.Entry<String, String> column : normal.entrySet()) {
            if (!column.getValue().equals(bot.get(column.getKey()))) {
                throw new SQLException("Missing or incompatible bot table column: " + column.getKey());
            }
        }
        String[] required = {"is_bot", "bot_score", "bot_reason", "device_class", "agent_name", "os_name",
                "search_engine", "search_engine_verify_status", "search_engine_ip_library_version"};
        for (String column : required) {
            if (!bot.containsKey(column)) {
                throw new SQLException("Missing bot table column: " + column);
            }
        }
    }

    /** 使用零行查询获取列名和类型，不读取或修改实际日志数据。 */
    private static Map<String, String> columns(Connection connection, String table) throws SQLException {
        Map<String, String> columns = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT * FROM " + table + " LIMIT 0")) {
            ResultSetMetaData metadata = result.getMetaData();
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                columns.put(metadata.getColumnName(i), metadata.getColumnTypeName(i));
            }
        }
        return columns;
    }
}
