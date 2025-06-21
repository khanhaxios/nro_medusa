FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy jar file
COPY game.jar /app/game.jar
COPY restart_game.sh /app/restart_game.sh

# Cho phép thực thi
RUN chmod +x /app/restart_game.sh

CMD ["/bin/bash", "-c", "/app/restart_game.sh"]