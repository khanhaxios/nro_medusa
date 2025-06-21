#!/bin/bash

# STEP 1: Run the Java client to send M_MAINTAIN
#!/bin/bash

echo "[INFO] Sending maintain command..."

cd /root/nro_medusa/ || { echo "Directory not found!"; exit 1; }

if [[ -f "ClientSendMaintain.java" ]]; then
    javac ClientSendMaintain.java
    java ClientSendMaintain
else
    echo "[ERROR] File ClientSendMaintain.java not found!"
    exit 1
fi


# STEP 2: Delay 30s
echo "[INFO] Waiting 30 seconds before restarting server..."
sleep 30

# STEP 3: Kill all tasks on port 14444
echo "[INFO] Killing any process on port 14444..."
PID_TO_KILL=$(lsof -ti:14445)
if [ -n "$PID_TO_KILL" ]; then
  kill -9 $PID_TO_KILL
  echo "[INFO] Killed process using port 14444 (PID: $PID_TO_KILL)"
else
  echo "[INFO] No process found on port 14444"
fi

# STEP 4: Run the server using run.sh
echo "[INFO] Restarting game server..."
chmod +x /root/nro_medusa/run.sh
/root/nro_medusa/run.sh

echo "[DONE] Server restarted successfully."
