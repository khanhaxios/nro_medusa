#!/bin/bash

echo "[INFO] Chờ MySQL online..."
until nc -z mysql 3306; do
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
echo "[INFO] Restarting game_server container..."
docker restart game_server || { echo "[ERROR] Failed to restart container!"; exit 1; }

echo "[DONE] Game server container restarted successfully."