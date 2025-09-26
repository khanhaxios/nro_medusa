#!/bin/bash

# Cấu hình
PORT=14445
DB_HOST=db
DB_PORT=3306

echo "⏳ Đợi MySQL (${DB_HOST}:${DB_PORT}) sẵn sàng..."

# Dùng netcat (nc) để kiểm tra DB
until nc -z -v -w30 $DB_HOST $DB_PORT
do
  echo "❌ Chưa kết nối được MySQL, thử lại sau 5s..."
  sleep 5
done

echo "✅ MySQL đã online, khởi động game trên port $PORT..."

exec java -Xms3g -Xmx3g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:InitiatingHeapOccupancyPercent=45 \
  -XX:+ParallelRefProcEnabled \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+DisableExplicitGC \
  -jar /app/nro_medusa.jar
