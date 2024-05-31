FROM alpine/helm:3.14.3

RUN mkdir /chart

# Assuming your Helm chart is in the /app directory
WORKDIR /chart

COPY helm-lint.sh /chart/container-script.sh

ENTRYPOINT ["/bin/sh", "/chart/container-script.sh"]