FROM openjdk:8-jre-alpine
RUN mkdir -p /opt/app
WORKDIR /opt/app
COPY ./run.sh ./src/main/resources/application.conf ./target/scala-2.12/data-provider-0.1.jar ./
ENTRYPOINT ["./run.sh"]