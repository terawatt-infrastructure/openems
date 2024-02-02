FROM ubuntu:latest
ENV DEBIAN_FRONTEND noninteractive
RUN mkdir -p /opt/openems-backend
WORKDIR /opt/openems-backend
COPY build/openems-backend.jar ./
COPY config.d ./config.d
COPY openems-backend.sh ./
RUN apt-get update
RUN apt-get install -y --no-install-recommends software-properties-common curl gpg-agent postgresql
RUN curl -sL https://repos.influxdata.com/influxdb.key | apt-key add -
RUN add-apt-repository -y ppa:openjdk-r/ppa

ENV ODOO_VERSION 15.0
ENV ODOO_RELEASE latest
RUN curl -o odoo.deb -sSL http://nightly.odoo.com/${ODOO_VERSION}/nightly/deb/odoo_${ODOO_VERSION}.${ODOO_RELEASE}_all.deb \
    && apt-get update \
    && apt-get -y install --no-install-recommends ./odoo.deb \
    && rm -rf /var/lib/apt/lists/* odoo.deb

RUN apt-get update && apt-get install -y openjdk-17-jdk openjdk-17-jre influxdb
RUN update-alternatives --config java
RUN update-alternatives --config javac
RUN chmod 777 ./openems-backend.sh
USER root
EXPOSE 8075-8080
ENTRYPOINT ["./openems-backend.sh"]

