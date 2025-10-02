FROM container-registry.oracle.com/graalvm/native-image:25-muslib AS nativebuild

COPY lib/maven/apache-maven-3.9.11-bin.tar.gz /opt/
RUN tar xzvf /opt/apache-maven-3.9.11-bin.tar.gz -C /opt/

ENV M2_HOME="/opt/apache-maven-3.9.11"
ENV PATH="${M2_HOME}/bin:$PATH"

RUN mkdir -p /opt/app-build
COPY ./pom.xml /opt/app-build/
COPY ./src /opt/app-build/src/

#COPY <<EOF /opt/script.sh
##!/bin/bash
#
#while true; do
#    sleep 10
#done
#EOF

WORKDIR /opt/app-build

RUN mvn -Pmusl clean package
#RUN chmod +x /opt/script.sh
#ENTRYPOINT ["/opt/script.sh"]


FROM alpine:3.22

RUN mkdir -p /opt/app \
    && addgroup -S non-root  \
    && adduser -S non-root -G non-root \
    && chown -R non-root:non-root /opt/app

COPY --from=nativebuild /opt/app-build/target/graal-vm-and-spring-boot /opt/app/

RUN chmod +x /opt/app/graal-vm-and-spring-boot

#COPY <<EOF /opt/script.sh
##!/bin/sh
#
#while true; do
#    sleep 10
#done
#EOF
#RUN chmod +x /opt/script.sh

EXPOSE 8080

USER non-root

#ENTRYPOINT ["/opt/script.sh"]
ENTRYPOINT ["/opt/app/graal-vm-and-spring-boot"]
