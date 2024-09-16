#!/bin/bash

APP="${PWD##*/}"

# Building docker image
./mvnw package -DskipTests
echo "Begin: Building docker image quarkus-BFF/$APP"
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/ci-bff-jvm .
echo "Done: Building docker image quarkus-BFF/$APP"
