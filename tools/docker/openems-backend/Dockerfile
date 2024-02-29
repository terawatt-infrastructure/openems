FROM ubuntu:latest
ENV DEBIAN_FRONTEND noninteractive
RUN mkdir -p /opt/openems-backend
WORKDIR /opt/openems-backend
COPY build/openems-backend.jar ./
COPY config.d ./initial_config
RUN apt-get update
RUN apt-get install -y --no-install-recommends software-properties-common curl gpg-agent postgresql jq
RUN curl -sL https://repos.influxdata.com/influxdb.key | apt-key add -
# RUN add-apt-repository -y ppa:openjdk-r/ppa

RUN apt-get update && apt-get install -y openjdk-17-jdk openjdk-17-jre influxdb
RUN update-alternatives --config java
RUN update-alternatives --config javac
USER root
EXPOSE 8075-8080
ENTRYPOINT ["/opt/openems-backend/openems-backend.sh"]

