#!/bin/bash

# Port game chạy
PORT=14445

# Kill hết process đang chiếm port
PID=$(lsof -t -i:$PORT)
if [ ! -z "$PID" ]; then
  echo "Killing process on port $PORT: $PID"
  kill -9 $PID
fi

# Start game
nohup java -jar ./game.jar > game.log 2>&1 &
