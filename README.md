# Master Of VALAWAI (MOV)

The Master of VALAWAI (MOV) implements the VALAWAI architecture as a central hub, 
maintaining its topology through a publisher/subscriber pattern using the RabbitMQ
message broker. By subscribing to all component channels, the MOV routes messages
based on this topology, ensuring component independence and decoupling.


## Summary

- Name: Master Of VALAWAI (MOV)
- Version: 1.4.0 (January 27, 2025)
- ASYNCAPI: [1.2.0 (May 9, 2024)](https://raw.githubusercontent.com/VALAWAI/MOV/ASYNCAPI_1.2.0/asyncapi.yml)
- OPENAPI: [1.2.0 (May 9, 2024)](https://raw.githubusercontent.com/VALAWAI/MOV/API_1.2.0/openapi.yml)
- Developed by: [IIIA-CSIC](https://www.iiia.csic.es)
- License: [GPL3](LICENSE)


## Get Started with Master of VALAWAI (MOV)

This section provides a streamlined approach to deploying the Master of VALAWAI (MOV)
leveraging Docker containers. Docker offers a popular software containerization platform
that enables applications to run in self-contained environments. This method simplifies
deployment and ensures consistency across various computing environments.

**Prerequisites**

Before deploying MOV, ensure you have the following software installed:

*   **Docker:** Follow the official instructions to install Docker:
 [https://docs.docker.com/get-started/get-docker/](https://docs.docker.com/get-started/get-docker/)
*   **Docker Compose:** Use the guide to install Docker Compose:
 [https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/)

**Steps to Deploy MOV**

Upon completion of the Docker installation process, initiate a terminal session. The
subsequent deployment procedures will be executed via the command-line interface of
the terminal using the following commands:

```bash
# Retrieve the latest stable MOV release
docker run -ti --rm -v ${HOME}:/root -v $(pwd):/git alpine/git clone https://github.com/VALAWAI/MOV.git

# Navigate to the MOV directory
cd MOV

# Launch the MOV (and build the Docker container if needed)
./runMOV.sh
```

The `runMOV.sh` script automates the MOV deployment process, including:

 *   Building the MOV Docker image
 *   Starting the required Docker containers for MOV, RabbitMQ, and MongoDB
 *   Configuring environment variables

**Access MOV User Interface and Management Interfaces**

Once the deployment script finishes, you can access the MOV user interface at:
[http://localhost:8080](http://localhost:8080)

**Important Security Note:**

The deployment script sets up RabbitMQ and MongoDB with default credentials (`mov:password`).
It is **strongly recommended** to change these default passwords immediately to enhance
security.

Here's how to access the management interfaces:

*   **RabbitMQ Management Interface:** http://localhost:8081 (Credentials: `mov:password`)
*   **Mongo Express:** http://localhost:8082 (Credentials: `mov:password`)

**Environment Variables for Configuration**

The MOV deployment can be configured using environment variables defined in a `.env` file,
typically located in the same directory as `runMOV.sh`. These variables allow you to override
default configurations.

**Available Environment Variables**

*   `RABBITMQ_TAG` (Default: `management`): Specifies the RabbitMQ Docker image tag.
*   `MQ_PORT` (Default: `5672`): Specifies the RabbitMQ listening port.
*   `MQ_UI_PORT` (Default: `8081`): Specifies the RabbitMQ management interface port.
*   `MQ_USER` (Default: `mov`): Specifies the RabbitMQ authentication username.
*   `MQ_PASSWORD` (Default: `password`): Specifies the RabbitMQ authentication
 password (**Change Immediately!**).
*   `MONGODB_TAG` (Default: `latest`): Specifies the MongoDB Docker image tag.
*   `MONGO_PORT` (Default: `27017`): Specifies the MongoDB listening port.
*   `MONGO_ROOT_USER` (Default: `root`): Specifies the MongoDB root user.
*   `MONGO_ROOT_PASSWORD` (Default: `password`): Specifies the MongoDB root user
 password (**Change Immediately!**).
*   `MONGO_LOCAL_DATA` (Default: `~/mongo_data/movDB`): Specifies the local directory
 for MongoDB data persistence.
*   `DB_NAME` (Default: `movDB`): Specifies the database name used by MOV in MongoDB.
*   `DB_USER_NAME` (Default: `mov`): Specifies the username MOV uses to access MongoDB.
*   `DB_USER_PASSWORD` (Default: `password`): Specifies the password MOV uses to access
 MongoDB (**Change Immediately!**).
*   `MOV_TAG` (Default: `latest`): Specifies the MOV Docker image tag.
*   `MOV_UI_PORT` (Default: `8080`): Specifies the MOV user interface port.

**Database Recreation**

The MOV database is created during the initial deployment. To modify database
parameters later, you'll need to recreate the database:

1.  Delete the directory specified by `MONGO_LOCAL_DATA`.
2.  Restart the MOV services using `./runMOV.sh`.

**Stop MOV Deployment**

To stop MOV and its dependent services (RabbitMQ and MongoDB), use:

```bash
./stopMOV.sh
```

This script is typically located in the same directory as `./runMOV.sh`.

## Generate Docker image

The recommended way to create a Docker image for the Master Of VALAWAI (MOV)
is to run the script:
 
 ```
./buildDockerImages.sh
```

This script will build the image and tag it with the component's version 
(e.g., `valawai/mov:1.4.0`).

The script offers several options for customization:

* **Specify tag:** Use `-t <tag>` or `--tag <tag>` to assign a custom tag name 
to the image (e.g., `./buildDockerImages.sh -t my-custom-image-name`).
* **Help message:** Use `-h` or `--help` to display a detailed explanation 
of all available options.

For example, to build an image with the tag `latest`, run:

```bash
./buildDockerImages.sh -t latest
```

This will create the container named `valawai/mov:latest`.

### Docker Environment Variables

Environment variables allow you to configure the MOV application's runtime
behavior within the Docker container without rebuilding the image. This section
describes the available environment variables:

#### RabbitMQ Configuration

*   `RABBITMQ_HOST` (Default: `mov-mq`): The hostname or IP address where the RabbitMQ
 service is listening.
*   `RABBITMQ_PORT` (Default: `5672`): The port on which the RabbitMQ service is listening.
*   `RABBITMQ_USERNAME` (Default: `mov`): The username for RabbitMQ authentication.
*   `RABBITMQ_PASSWORD` (Default: `password`): **CRITICAL:** Change this default password
 immediately for security reasons.

#### MongoDB Configuration

*   `QUARKUS_MONGODB_HOSTS` (Default: `mongo:27017`): The hostname(s) or connection string
 for the MongoDB server.
*   `QUARKUS_MONGODB_DATABASE` (Default: `movDB`): The name of the MongoDB database used
 by the application.
*   `QUARKUS_MONGODB_CREDENTIALS_USERNAME` (Default: `mov`): The username for MongoDB authentication.
*   `QUARKUS_MONGODB_CREDENTIALS_PASSWORD` (Default: `password`): **CRITICAL:** Change this default
 password immediately for security reasons.

#### Other Configuration


*   `MOV_URL` (Default: `http://localhost:8080`): The URL where the MOV user interface will be accessible.

**Security Best Practices:**

*   If an environment variable is not set, the application will use its default value.
*   For production deployments, **never** store passwords directly in environment variables,
Dockerfiles, or scripts. Use Docker secrets, a dedicated secrets management service 
(e.g., HashiCorp Vault), or other secure configuration mechanisms to manage sensitive information.


## Development Environment

This section summarizes how to set up and use the development environment
for the Master Of VALAWAI (MOV) project. It covers starting and stopping
the environment, running the application in development mode, executing tests,
and accessing key services.

To begin, start the development environment by executing the `./startDevelopmentEnvironment.sh` script.
This launches a pre-configured Docker container containing all necessary dependencies
and starts supporting services.

Once the environment is running, you can interact with the MOV application. To start
the MOV application in development mode, use the command `quarkus dev` or `startServer`. 
This enables automatic recompilation and deployment of code changes, allowing for rapid
iteration. The application's user interface can then be accessed in your web browser
 at [http://localhost:8080/](http://localhost:8080/).

Several Maven commands are available for running tests. Use `mvn test` to run all unit and
integration tests. For debugging during test execution, use `mvnd test`. To run tests specifically
using the development MongoDB instance, use `mvn -DuseDevDatabase=true test`. Similarly, 
use `mvn -useDevMQ=true test` to run tests using the development RabbitMQ instance.

The development environment includes several key services. RabbitMQ, a message broker used
for communication between application components, is accessible at 
[http://localhost:8081/](http://localhost:8081/) with credentials `mov:password`. MongoDB,
the application's NoSQL database named `movDB`, uses the default credentials `mov:password`. 
Mongo Express, a web-based interface for interacting with the MongoDB database, is accessible
at [http://localhost:8082/](http://localhost:8082/) with credentials `mov:password`.

Finally, to stop the development environment, either type `exit` within the Docker container's
terminal or execute the `./stopDevelopmentEnvironment.sh` script from your host machine's terminal.
    
## Links

 - [Master Of VALAWAI tutorial](https://valawai.github.io/docs/tutorials/mov)
 - [VALWAI documentation](https://valawai.github.io/docs/)
 - [VALAWAI project web site](https://valawai.eu/)
 - [Twitter](https://twitter.com/ValawaiEU)
 - [GitHub](https://github.com/VALAWAI)