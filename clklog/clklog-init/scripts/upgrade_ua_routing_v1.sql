-- Run explicitly after substituting CLKLOG_LOG_DB. No historical rows are copied.
CREATE TABLE IF NOT EXISTS ${CLKLOG_LOG_DB}.log_analysis_bot AS ${CLKLOG_LOG_DB}.log_analysis;

ALTER TABLE ${CLKLOG_LOG_DB}.log_analysis_bot
    ADD COLUMN IF NOT EXISTS is_bot UInt8 DEFAULT 1,
    ADD COLUMN IF NOT EXISTS bot_score UInt8 DEFAULT 100,
    ADD COLUMN IF NOT EXISTS bot_reason Array(String),
    ADD COLUMN IF NOT EXISTS device_class String,
    ADD COLUMN IF NOT EXISTS agent_name String,
    ADD COLUMN IF NOT EXISTS os_name String,
    ADD COLUMN IF NOT EXISTS search_engine String,
    ADD COLUMN IF NOT EXISTS search_engine_verify_status String,
    ADD COLUMN IF NOT EXISTS search_engine_ip_library_version String;
