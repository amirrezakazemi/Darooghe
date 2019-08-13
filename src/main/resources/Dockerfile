FROM openjdk:8-jre-alpine
RUN mkdir -p /opt/app
WORKDIR /opt/app
COPY ./run.sh ./application.conf ./Darooghe-assembly-0.1.jar ./
ENTRYPOINT ["./run.sh"]