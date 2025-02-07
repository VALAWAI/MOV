#!/bin/bash
function docker_git () {
    (docker run -ti --rm -v ${HOME}:/root -v $(pwd):/git alpine/git "$@")
}

if [ -f /.dockerenv ]; then
   echo "You can not start the Master Of VALAWAI inside a docker container"
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	pushd $DIR >/dev/null

	image_name="valawai/mov:latest"
	if docker images -q $image_name >/dev/null; then
		image_timestamp=$(docker inspect -f '{{ .Created }}' $image_name) 
	    latest_commit_timestamp=$(docker_git shortlog -1 --since=\"$image_timestamp\")
        if [ $(echo "$latest_commit_timestamp" | wc -l) -gt 1 ] ; then
            echo "Docker image $image_name is older than last commit. Rebuilding."
			# The image is older that the last modified file
			./buildDockerImages.sh -t latest
		fi

	else
		# No image exist
		./buildDockerImages.sh -t latest
	fi

    DOCKER_PARAMS="-f src/main/docker/docker-compose.yml"
	if [[ -e '.env' ]]; then

		DOCKER_PARAMS="$DOCKER_PARAMS --env-file .env"
	fi
	docker compose $DOCKER_PARAMS up -d
fi
