# Master Of VALAWAI (MOV)

The Master of VALAWAI (MOV) serves as the central hub of the VALAWAI architecture.
It maintains the system's topology and facilitates communication between components 
using a publisher/subscriber pattern via the RabbitMQ message broker.  By subscribing 
to all component channels, MOV intelligently routes messages based on the defined 
topology, promoting component independence and decoupling.

## Summary

*   **Name:** Master Of VALAWAI (MOV)
*   **Version:** 2.0.0 (August 6, 2025)
*   **ASYNCAPI:** [1.2.0 (May 9, 2024)](https://raw.githubusercontent.com/VALAWAI/MOV/ASYNCAPI_1.2.0/asyncapi.yml)
*   **OPENAPI:** [2.0.0 (August 6, 2025)](https://raw.githubusercontent.com/VALAWAI/MOV/API_2.0.0/openapi.yml)
*   **Developed by:** [IIIA-CSIC](https://www.iiia.csic.es)
*   **License:** [GPL3](LICENSE)

## Getting Started

This is a concise guide on how to get MOV up and running. For a comprehensive deployment 
guide, consult the 
[VALAWAI documentation](https://valawai.github.io/docs/architecture/implementations/mov/deploy).

### Prerequisites

Before you begin, you need to **install Docker** by following the 
[official instructions](https://docs.docker.com/get-docker/). Docker is essential for running 
the MOV application and its dependencies.

### Installation and Setup

1. **Clone the MOV Repository:**

First, open a terminal and clone the MOV repository from GitHub. The `docker run` command below 
uses an `alpine/git` container to perform the clone operation.

```bash
docker run -ti --rm -v ${HOME}:/root -v $(pwd):/git alpine/git clone [https://github.com/VALAWAI/MOV.git](https://github.com/VALAWAI/MOV.git)
```

2. **Navigate to the MOV directory:**

After the repository is cloned, navigate into the new directory.

```bash
cd MOV
```

3. **Run MOV:**

Execute the `runMOV.sh` script to start the application. This script automates several steps for you.

 ```bash
 ./runMOV.sh
 ```

This single command performs the following actions:

* Builds the necessary **MOV Docker image**.
* Starts the Docker containers for **MOV**, **RabbitMQ**, and **MongoDB**.
* Provides access to the **MOV User Interface (UI)** at  [http://localhost:8080](http://localhost:8080)
* Also provides access to the **RabbitMQ Management Interface** at [http://localhost:8081](http://localhost:8081) using the credentials  `mov:password`.

### Customizing the Configuration

You can customize the default behavior of the `runMOV.sh` script by creating a file named `.env`
in the same directory. This file allows you to override default environment variables. For a full 
list of available variables, refer to the [VALAWAI docuemntation](https://valawai.github.io/docs/architecture/implementations/mov/deploy#environment-variables).

Here are some of the most important environment variables you can set:

* `MOV_UI_PORT` (Default: `8080`): The port where the MOV UI is accessible.
* `MOV_URL` (Default: `http://localhost:8080`): The public URL where the MOV UI is visible.
* `MONGO_LOCAL_DATA` (Default: `~/.mongo_data/movDB`): The local directory used to store 
the MongoDB database files.

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

 - [Master Of VALAWAI documentstion](https://valawai.github.io/docs/architecture/implementations/mov/)
 - [VALWAI documentation](https://valawai.github.io/docs/)
 - [VALAWAI project web site](https://valawai.eu/)
 - [Twitter](https://twitter.com/ValawaiEU)
 - [GitHub](https://github.com/VALAWAI)