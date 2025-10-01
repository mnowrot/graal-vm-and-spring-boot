FROM ubuntu:24.04

RUN mkdir -p /opt/app

RUN groupadd -r non-root  \
    && useradd -r non-root -g non-root \
    && chown -R non-root:non-root /opt/app

USER non-root

COPY ./graal-vm-and-spring-boot /opt/app/
COPY ./*.so /opt/app/

RUN chmod +x /opt/app/graal-vm-and-spring-boot

ENTRYPOINT ["/opt/app/graal-vm-and-spring-boot"]