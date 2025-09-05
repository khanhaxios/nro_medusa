#!/bin/bash

#
# Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
#

# Port game chạy
PORT=14445

# Kill hết process đang chiếm port
PID=$(lsof -t -i:$PORT)
if [ ! -z "$PID" ]; then
  echo "Killing process on port $PORT: $PID"
  kill -9 $PID
fi

# Start game
nohup java -jar ./gameplayopen.jar > game.log 2>&1 &
