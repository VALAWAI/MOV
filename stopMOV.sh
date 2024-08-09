#!/bin/bash
if [ -f /.dockerenv ]; then
   echo "You can not start the Master Of VALAWAI inside a docker container"
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	pushd $DIR >/dev/null

	docker compose -f src/main/docker/docker-compose.yml down
fi
