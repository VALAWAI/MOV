# Master Of VALAWAI (MOV)

The [Value-Aware Artificial Intelligence](https://valawai.eu/) project aims at developing 
value-aware AI systems; that is, AI systems that can understand and abide
by a value system and explain their behaviour or understand the behaviour of
others in terms of a value system. For this purpose, VALAWAI defines an
[architecture](https://valawai.github.io/docs/toolbox/architecture/) inspired
in the [Global Neuronal Workspace](https://valawai.github.io/docs/toolbox/architecture/gnw).
This is divided into three layers, the C0, C1 and C2 layers.

The C0 layer contains components are components that are concerned with information
extraction and task execution. They process the world and forward this information
to the C1 components.

The C1 layer contains components that are responsible for integrating and analysing
the information coming from C0 components and making decisions accordingly. 

The C2 is the reflective layer analysing the value alignment of behaviour. These components
constitute the main novelty of VALAWAI, and it is here that the reasoning with values will manifest,
requiring capabilities such as value acquisition, value representation and reasoning,
value-alignment mechanisms and value-driven explainability.

These components interact between them exchanging messages that contain data or
actions that they must do. In both cases, the path that a message has to follow
is defined by a topology. The piece of software that maintains this topology
is the Master Of VALAWAI (MOV). To succeed in this task, the MOV is connected
to a message queue, at the moment [RabbitMQ](https://www.rabbitmq.com/),
and listens for any messages published by any VALAWAI components and following
the topology decides which components have to receive it. This is similar to
an Internet router but instead of the address specified in the message is
the topology inside the MOV that decides the route that a message has to follow.


## Summary

- Name: Master Of VALAWAI (MOV)
- Version: 1.2.0 (May 2, 2024)
- ASYNCAPI: [1.2.0 (May 2, 2024)](https://raw.githubusercontent.com/VALAWAI/MOV/ASYNCAPI_1.2.0/asyncapi.yml)
- OPENAPI: [1.2.0 (May 2, 2024)](https://raw.githubusercontent.com/VALAWAI/MOV/API_1.2.0/openapi.yml)
- Developed by: [IIIA-CSIC](https://www.iiia.csic.es)
- License: [GPL3](LICENSE)


## Get started with MOV

The easier way to run the Master of VALAWAI is by executing a script defined
on the source repository. The next steps explain how to do it:

1. Install [docker](https://docs.docker.com/get-docker/), [docker compose](https://docs.docker.com/compose/install/)
 and [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).

2. Get the code from GitHub (``git clone https://github.com/VALAWAI/MOV.git``)
3. Execute the script (``./runMOV.sh``)

After that, if you open a browser and go to [http://localhost:8080](http://localhost:8080)
you can view the MOV user interface. Also, you can access the RabbitMQ user interface
at [http://localhost:8081](http://localhost:8081). The user credentials for this last
one are **mov:password**.

You can read more about MOV on the [tutorial](https://valawai.github.io/docs/tutorials/mov).


### Dependencies

The Master Of VALAWAI has the following software dependencies:

- [RabbitMQ](https://www.rabbitmq.com/) the broker that is used to exchange
 messages between the VALAWAi components.
- [MongoDB](https://www.mongodb.com/) the database used to store information.


## Development

You can start the development environment with the script:

```shell script
./startDevelopmentEnvironment.sh
```

After that, you have a bash shell where you can interact with
the Quarkus development environment. You can start the development
server with the command:

```shell script
startServer
```

Alternatively, to run the test using the started Quarkus client, you can use Maven.

 * __mvn test__  to run all the tests
 * __mvnd test__  to run all the tests on debugging mode.
 * __mvn -DuseDevDatabase=true test__  to run all the tests using the started database,
 	instead of an independent container.

Also, this starts the tools:

 * __RabbitMQ__  the server to manage the messages to interchange with the components.
 The management web interface can be opened at **http://localhost:8081** with the credential
 **mov**:**password**.
 * __MongoDB__  the database to store the data used by the MOV. The database is named as **movDB** and the user credentials **mov:password**.
 The management web interface can be opened at **http://localhost:8081** with the credential
 **mov**:**password**.
 * __Mongo express__  the web interface to interact with the MongoDB. The web interface
  can be opened at **http://localhost:8082**.


## Links

 - [Master Of VALAWAI tutorial](https://valawai.github.io/docs/tutorials/mov)
 - [VALWAI documentation](https://valawai.github.io/docs/)
 - [VALAWAI project web site](https://valawai.eu/)
 - [Twitter](https://twitter.com/ValawaiEU)
 - [GitHub](https://github.com/VALAWAI)