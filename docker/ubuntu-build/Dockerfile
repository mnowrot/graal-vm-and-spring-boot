FROM ubuntu:24.04

RUN mkdir -p /opt/app \
    && groupadd -r non-root  \
    && useradd -r non-root -g non-root \
    && chown -R non-root:non-root /opt/app

COPY ./@project.artifactId@ /opt/app/
COPY ./*.so /opt/app/

RUN chmod +x /opt/app/@project.artifactId@

EXPOSE 8080

USER non-root

ENTRYPOINT ["/opt/app/@project.artifactId@"]