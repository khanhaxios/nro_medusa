FROM eclipse-temurin:17-jdk

# Cài lsof + netcat để test port
RUN apt-get update && apt-get install -y lsof netcat-openbsd && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY nro_medusa.jar /app/nro_medusa.jar
COPY data /app/data
COPY lib /app/lib
COPY run.sh /app/run.sh
COPY wait-for-it.sh /app/wait-for-it.sh

RUN chmod +x /app/run.sh /app/wait-for-it.sh

CMD ["/bin/bash", "/app/run.sh"]
