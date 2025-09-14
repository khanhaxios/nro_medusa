#!/bin/bash

# Port game chạy
PORT=14445

echo "Starting game on port $PORT..."

exec java -Xms3g -Xmx3g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:InitiatingHeapOccupancyPercent=45 \
  -XX:+ParallelRefProcEnabled \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+DisableExplicitGC \
  -jar /app/nro_medusa.jar
