#!/bin/bash

source ../framework.sh

echo "starting BFF"
docker-compose --file docker-compose-bff.yml up -d

wait_on_health_bff http://localhost:8080

echo "BFF started"