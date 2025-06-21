FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy jar file
COPY game.jar /app/game.jar
COPY restart_game.sh /app/restart_game.sh

RUN apt update && apt install -y netcat
# Cho phép thực thi
RUN chmod +x /app/restart_game.sh

CMD ["bash", "/app/restart_game.sh"]