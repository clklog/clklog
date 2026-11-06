-- Run explicitly after substituting CLKLOG_LOG_DB. Existing rows retain the default empty value.
ALTER TABLE ${CLKLOG_LOG_DB}.log_analysis_bot
    ADD COLUMN IF NOT EXISTS agent_class String DEFAULT '';
