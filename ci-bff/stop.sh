#!/bin/bash

echo "stopping BFF"
docker-compose --file docker-compose-bff.yml down

echo "BFF stopped"
