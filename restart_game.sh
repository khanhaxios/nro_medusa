#!/bin/bash

until mysql -h mysql -u"$DB_USER" -p"$DB_PASSWORD" -e "USE $DB_NAME;" >/dev/null 2>&1; do
  echo "🕐 Đợi MySQL sẵn sàng..."
  sleep 2
done

echo "[INFO] Sending maintain command..."

cd /root/nro_medusa/ || { echo "Directory not found!"; exit 1; }

if [[ -f "ClientSendMaintain.java" ]]; then
    javac ClientSendMaintain.java
    java ClientSendMaintain
else
    echo "[ERROR] File ClientSendMaintain.java not found!"
    exit 1
fi

echo "[INFO] Waiting 30 seconds before restarting container..."
sleep 30

# Restart the container instead of killing processes manually
echo "[INFO] Restarting game server inside the same container..."
exec java -jar /app/game.jar

echo "[DONE] Game server container restarted successfully."