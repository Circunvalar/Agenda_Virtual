#!/bin/sh
set -e
# Use PORT injected by platform (Render, Heroku, etc.) if present, otherwise APP_PORT
if [ -n "$PORT" ]; then
  RUNTIME_PORT="$PORT"
else
  RUNTIME_PORT="$APP_PORT"
fi

echo "Starting application on port $RUNTIME_PORT"
exec java -jar app.jar --server.port="$RUNTIME_PORT"

