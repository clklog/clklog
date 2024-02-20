#!/bin/sh

set -e

exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -jar /app.jar "$@"