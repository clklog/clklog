#!/bin/bash

mkdir -p clklog_dc_data/redis
chown 999 clklog_dc_data/redis
mkdir -p   clklog_dc_data/processing/checkpoints
chown 9999 clklog_dc_data/processing/checkpoints
chgrp 9999 clklog_dc_data/processing/checkpoints
mkdir -p   clklog_dc_data/zookeeper
chown 1001 clklog_dc_data/zookeeper
chgrp 1001 clklog_dc_data/zookeeper
mkdir -p   clklog_dc_data/kafka
chown 1001 clklog_dc_data/kafka
chgrp 1001 clklog_dc_data/kafka
