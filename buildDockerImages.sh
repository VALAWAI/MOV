#!/bin/bash
if ! docker stats --no-stream >/dev/null 2>&1; then
    echo "Docker does not seem to be running, run it first and retry"
    exit 1
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	pushd $DIR > /dev/null
	TAG=$(grep --max-count=1 '<version>' pom.xml | awk -F '>' '{ print $2 }' | awk -F '<' '{ print $1 }')

	DOCKER_ARGS=""
	while [[ $# -gt 0 ]]; do
      case $1 in
        -t|--tag)
          TAG="$2"
          shift # past argument
          shift # past value
          ;;
        -h|--help*)
          echo "	-t|--tag <tag>			Build a docker image with a the **<tag>** name."
          echo "	-h|--help			Show a help message that explains these parameters."
          exit 0
          ;;
        *)
          echo "Unknown option $1"
          exit 1
          ;;
      esac
    done

	FILE_DATE=$(date -r src/dev/docker/Dockerfile +%s)
	IMAGE_DATE=$(docker inspect --format='{{.Created}}' valawai/mov:dev 2>/dev/null)
	if [ $? -ne 0 ]; then
		IMAGE_DATE=0
	else
		IMAGE_DATE=$(echo $IMAGE_DATE|date +%s)
	fi
	if [ $FILE_DATE -ge $IMAGE_DATE ]; then
		DOCKER_BUILDKIT=1 docker build --pull -f src/dev/docker/Dockerfile -t valawai/mov:dev .
	fi

    DOCKER_ARGS="$DOCKER_ARGS --rm --name mov_build_docker_image --add-host=host.docker.internal:host-gateway -v /var/run/docker.sock:/var/run/docker.sock"
	docker run $DOCKER_ARGS -v "${HOME}/.m2":/root/.m2  -v "${PWD}":/app valawai/mov:dev ./mvnw clean package -DskipTests -Dquarkus.container-image.tag=$TAG
	if [ $? -ne 0 ]; then
		echo "Cannot build docker image"
		popd > /dev/null
		exit 1
	else
		popd > /dev/null
		exit 0
	fi
fi
