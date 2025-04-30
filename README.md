# Master Of VALAWAI (MOV)

The Master of VALAWAI (MOV) serves as the central hub of the VALAWAI architecture.
It maintains the system's topology and facilitates communication between components 
using a publisher/subscriber pattern via the RabbitMQ message broker.  By subscribing 
to all component channels, MOV intelligently routes messages based on the defined 
topology, promoting component independence and decoupling.

## Summary

*   **Name:** Master Of VALAWAI (MOV)
*   **Version:** 1.5.0 (April 30, 2025)
*   **ASYNCAPI:** [1.2.0 (May 9, 2024)](https://raw.githubusercontent.com/VALAWAI/MOV/ASYNCAPI_1.2.0/asyncapi.yml)
*   **OPENAPI:** [1.2.0 (May 9, 2024)](https://raw.githubusercontent.com/VALAWAI/MOV/API_1.2.0/openapi.yml)
*   **Developed by:** [IIIA-CSIC](https://www.iiia.csic.es)
*   **License:** [GPL3](LICENSE)

## Getting Started

This section provides instructions for deploying MOV using Docker.  Docker simplifies
deployment by containerizing the application and ensuring consistency across 
different environments.

### Prerequisites

*   **Docker:** Install Docker following the official instructions: 
[https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/)
*   **Docker Compose:** Install Docker Compose: 
[https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/)

### Deployment Steps

1.  **Clone the Repository:**

```bash
docker run -ti --rm -v ${HOME}:/root -v $(pwd):/git alpine/git clone [https://github.com/VALAWAI/MOV.git](https://github.com/VALAWAI/MOV.git)
```

2.  **Navigate to the MOV Directory:**

```bash
cd MOV
```

3.  **Run MOV:**

 ```bash
 ./runMOV.sh
 ```

The `runMOV.sh` script automates the following:

*   Builds the MOV Docker image.
*   Starts Docker containers for MOV, RabbitMQ, and MongoDB.
*   Configures environment variables.

### Accessing the Interfaces

*   **MOV User Interface:** [http://localhost:8080](http://localhost:8080)
*   **RabbitMQ Management Interface:** [http://localhost:8081](http://localhost:8081) (Credentials: `mov:password`)
*   **Mongo Express:** [http://localhost:8082](http://localhost:8082) (Credentials: `mov:password`)

**Security Notice:** The default credentials for RabbitMQ and MongoDB are `mov:password`.  
**Change these immediately** for production deployments.

### Configuration with Environment Variables

MOV can be configured using a `.env` file located in the same directory as `runMOV.sh`.  
The following environment variables are available:

*   `RABBITMQ_TAG` (Default: `management`): Specifies the RabbitMQ Docker image tag.
*   `MQ_PORT` (Default: `5672`): Specifies the RabbitMQ listening port.
*   `MQ_UI_PORT` (Default: `8081`): Specifies the RabbitMQ management interface port.
*   `MQ_USER` (Default: `mov`): Specifies the RabbitMQ authentication username.
*   `MQ_PASSWORD` (Default: `password`): Specifies the RabbitMQ authentication password 
(**Change Immediately!**).
*   `MONGODB_TAG` (Default: `latest`): Specifies the MongoDB Docker image tag.
*   `MONGO_PORT` (Default: `27017`): Specifies the MongoDB listening port.
*   `MONGO_ROOT_USER` (Default: `root`): Specifies the MongoDB root user.
*   `MONGO_ROOT_PASSWORD` (Default: `password`): Specifies the MongoDB root user password 
(**Change Immediately!**).
*   `MONGO_LOCAL_DATA` (Default: `~/mongo_data/movDB`): Specifies the local directory for 
MongoDB data persistence.
*   `DB_NAME` (Default: `movDB`): Specifies the database name used by MOV in MongoDB.
*   `DB_USER_NAME` (Default: `mov`): Specifies the username MOV uses to access MongoDB.
*   `DB_USER_PASSWORD` (Default: `password`): Specifies the password MOV uses to access 
MongoDB (**Change Immediately!**).
*   `MOV_TAG` (Default: `latest`): Specifies the MOV Docker image tag.
*   `MOV_UI_PORT` (Default: `8080`): Specifies the MOV user interface port.

### Database Recreation

The MOV database is created during the initial deployment. To modify database parameters, 
you'll need to recreate the database:

1.  Delete the directory specified by `MONGO_LOCAL_DATA`.
2.  Restart the MOV services using `./runMOV.sh`.

### Stopping MOV Deployment

To stop MOV and its dependent services (RabbitMQ and MongoDB), use:

```bash
./stopMOV.sh
```

This script is typically located in the same directory as `./runMOV.sh`.

## Generating the Docker Image

The recommended way to create a Docker image for MOV is to run the following script:

```bash
./buildDockerImages.sh
```

This script builds the image and tags it with the component's version 
(e.g., `valawai/mov:1.4.1`).

The script offers several options:

- `-t <tag>` or `--tag <tag>`: Assigns a custom tag name to the image 
(e.g., `./buildDockerImages.sh -t my-custom-image-name`).
- `-h` or `--help`: Displays a detailed explanation of all available options.

For example, to build an image with the tag latest, run:

```bash
./buildDockerImages.sh -t latest
```

This creates the container named `valawai/mov:latest`.

### Docker Environment Variables (Runtime Configuration)

Environment variables configure MOV's runtime behavior within the Docker 
container without rebuilding the image.

#### RabbitMQ Configuration

*   `RABBITMQ_HOST` (Default: `mov-mq`): The hostname or IP address of the 
RabbitMQ service.
*   `RABBITMQ_PORT` (Default: `5672`): The port RabbitMQ is listening on.
*   `RABBITMQ_USERNAME` (Default: `mov`): The username for RabbitMQ authentication.
*   `RABBITMQ_PASSWORD` (Default: `password`): **CRITICAL:** Change this default 
password immediately for security reasons.

#### MongoDB Configuration

*   `QUARKUS_MONGODB_HOSTS` (Default: `mongo:27017`): The hostname(s) or connection 
string for the MongoDB server.
*   `QUARKUS_MONGODB_DATABASE` (Default: `movDB`): The name of the MongoDB database.
*   `QUARKUS_MONGODB_CREDENTIALS_USERNAME` (Default: `mov`): The username for MongoDB 
authentication.
*   `QUARKUS_MONGODB_CREDENTIALS_PASSWORD` (Default: `password`): **CRITICAL:** Change 
this default password immediately for security reasons.

#### Other Configuration

*   `MOV_URL` (Default: `http://localhost:8080`): The URL where the MOV user interface 
is accessible.

**Security Best Practices:**

*   If an environment variable is not set, the application uses its default 
value.
*   For production deployments, **never** store passwords directly in environment 
variables, Dockerfiles, or scripts. Use Docker secrets, a dedicated secrets 
management service (e.g., HashiCorp Vault), or other secure configuration 
mechanisms.

## Development Environment

This section describes setting up and using the MOV development environment.

1.  **Start the Environment:** `./startDevelopmentEnvironment.sh` launches a Docker 
container with dependencies and starts supporting services.

2.  **Run MOV in Development Mode:** `startServer` enables automatic 
recompilation and deployment. Access the UI at [http://localhost:8080/](http://localhost:8080/).
For debugging, connect your Java debugger to port `5005`.

3.  **Run Tests:**
    *   `mvn test`: Runs all unit and integration tests.
    *   `mvnd test`: Runs tests with debugging enabled.
    *   `mvn -DuseDevDatabase=true test`: Runs tests using the development 
    MongoDB instance.
    *   `mvn -DuseDevMQ=true test`: Runs tests using the development RabbitMQ instance.

4.  **Access Services:**
    *   RabbitMQ: [http://localhost:8081/](http://localhost:8081/) 
    (Credentials: `mov:password`)
    *   MongoDB (movDB): Credentials: `mov:password`
    *   Mongo Express: [http://localhost:8082/](http://localhost:8082/) (Credentials: `mov:password`)

5.  **Stop the Environment:** Type `exit` in the Docker container's terminal 
or execute `./stopDevelopmentEnvironment.sh` from your host machine.


## Links

 - [Master Of VALAWAI tutorial](https://valawai.github.io/docs/tutorials/mov)
 - [VALWAI documentation](https://valawai.github.io/docs/)
 - [VALAWAI project web site](https://valawai.eu/)
 - [Twitter](https://twitter.com/ValawaiEU)
 - [GitHub](https://github.com/VALAWAI)