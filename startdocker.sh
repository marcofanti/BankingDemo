#!/bin/bash

docker stop securebank
docker rm securebank

docker rmi $(docker images -f "dangling=true" -q)

docker build -t securebank .

 # Run – env vars are passed in from .env
 docker run -d --name securebank --env-file .env -p 8080:8080 securebank