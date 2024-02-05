#!/bin/bash
if [ -f /.dockerenv ]; then
   echo "You can not start the Master Of VALAWAI inside a docker container"
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	pushd $DIR >/dev/null

	if [ -z "$(docker images -q valawai/mov:latest> /dev/null)" ]; then
		./buildDockerImages.sh latest
	fi
	docker-compose -f src/main/docker/docker-compose.yml up -d
fi
