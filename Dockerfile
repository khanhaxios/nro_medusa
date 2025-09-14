FROM eclipse-temurin:17-jdk

# Cài lsof
RUN apt-get update && apt-get install -y lsof && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY nro_medusa.jar /app/nro_medusa.jar
COPY data /app/data
COPY lib /app/lib
COPY run.sh /app/run.sh

RUN chmod +x /app/run.sh

CMD ["/bin/bash", "/app/run.sh"]
