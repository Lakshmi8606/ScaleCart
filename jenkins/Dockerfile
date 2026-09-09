FROM jenkins/jenkins:lts

USER root

# docker CLI only — daemon is the host, via /var/run/docker.sock
RUN apt-get update \
    && apt-get install -y --no-install-recommends docker.io docker-cli \
    && rm -rf /var/lib/apt/lists/*
