-- Run explicitly after substituting CLKLOG_LOG_DB. No historical rows are copied.
CREATE TABLE IF NOT EXISTS ${CLKLOG_LOG_DB}.log_analysis_bot AS ${CLKLOG_LOG_DB}.log_analysis;

ALTER TABLE ${CLKLOG_LOG_DB}.log_analysis_bot
    ADD COLUMN IF NOT EXISTS is_bot UInt8 DEFAULT 1,
    ADD COLUMN IF NOT EXISTS bot_score UInt8 DEFAULT 100,
    ADD COLUMN IF NOT EXISTS bot_reason Array(String),
    ADD COLUMN IF NOT EXISTS device_class String,
    ADD COLUMN IF NOT EXISTS agent_class String,
    ADD COLUMN IF NOT EXISTS search_engine String,
    ADD COLUMN IF NOT EXISTS search_engine_verify_status String,
    ADD COLUMN IF NOT EXISTS ai_verify_status String,
    ADD COLUMN IF NOT EXISTS bot_category String DEFAULT '',
    ADD COLUMN IF NOT EXISTS bot_subcategory String DEFAULT '';

-- bot 表的浏览器/操作系统复用 log_analysis 的 browser/os（Yauaa AGENT_NAME/OS_NAME，上报值优先），
-- 不再单独建 agent_name/os_name 列，在此通过列备注标记对应关系。
ALTER TABLE ${CLKLOG_LOG_DB}.log_analysis_bot
    MODIFY COLUMN browser String COMMENT '浏览器(=Yauaa AGENT_NAME；bot 表复用此列，替代已废弃的 agent_name)',
    MODIFY COLUMN os String COMMENT '操作系统(=上报值或Yauaa OS_NAME；bot 表复用此列，替代已废弃的 os_name)';

-- The IP library version column is no longer written; drop it from databases
-- created by earlier revisions of this script.
ALTER TABLE ${CLKLOG_LOG_DB}.log_analysis_bot DROP COLUMN IF EXISTS search_engine_ip_library_version;

-- agent_name/os_name 已废弃（与 browser/os 语义重复），统一改读 browser/os 列。
ALTER TABLE ${CLKLOG_LOG_DB}.log_analysis_bot DROP COLUMN IF EXISTS agent_name;
ALTER TABLE ${CLKLOG_LOG_DB}.log_analysis_bot DROP COLUMN IF EXISTS os_name;
