FROM openjdk:8-jre-alpine
RUN mkdir -p /opt/app
WORKDIR /opt/app
COPY ./run.sh ./target/scala-2.12/spark-analytics-0.1.jar ./
ENTRYPOINT ["./run.sh"]