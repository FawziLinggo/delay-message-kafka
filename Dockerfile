FROM openjdk:17-alpine
#RUN echo "192.568.325.521neo4j" > "/etc/hosts"
#RUN cat "/etc/hosts"

RUN ls
COPY src/main/resources/consumer-dcoker.properties /consumer-dcoker.properties
COPY target/delay-datafeed-idx-1.0-SNAPSHOT-jar-with-dependencies.jar /delay.jar

CMD ["java", "-cp", "/delay.jar", "docker.DelayDocker", "--config.delay.path", "/consumer-dcoker.properties"]