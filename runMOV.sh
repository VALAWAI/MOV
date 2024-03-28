#!/bin/bash
if [ -f /.dockerenv ]; then
   echo "You can not start the Master Of VALAWAI inside a docker container"
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	pushd $DIR >/dev/null

	if [ -z "$(docker images -q valawai/mov:latest)" ]; then
		# No image exist
		./buildDockerImages.sh latest

	else

		SRC_DATE=$(TZ=UTC0 git log -1 --quiet --date=local --format="%cd" --date=format-local:'%Y-%m-%dT%H:%M:%S.000000000Z')
		IMG_DATE=$(docker inspect -f '{{ .Created }}' valawai/mov:latest)
		if [[ $SRC_DATE > $IMG_DATE ]]; then
			# The image is older that the last modified file
			./buildDockerImages.sh latest
		fi
	fi

	docker-compose -f src/main/docker/docker-compose.yml up -d
fi
