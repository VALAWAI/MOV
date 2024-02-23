# Master Of VALAWAI (MOV)

This component is responsible for maintaining the topology
and routing the messages between the components of the RGNW toolbox.


# Summary

- Version: 0.1.0 (October 26,2023)
- API: [0.1.0 (October 26, 2023)](./asyncapi.yml)
- Developed by: IIIA-CSIC
- License: [MIT](LICENSE)

# Introduction


# Development

You can start the development environment with the script:

```shell script
./startDevelopmentEnvironment.sh
```

After that you have a bash shell where you can interact with
the quarkus development environment. You can start the development
server with the command:

```shell script
startServer
```

Alternative to run the test using the started quarkus client, you can use maven.

 * __mvn test__  to run all the tests
 * __mvnd test__  to run all the tests on debugging mode.
 * __mvn -DuseDevDatabase=true test__  to run all the tests using the started database,
 	intead an independent container.

Also, this starts the tools:

 * __RabbitMQ__  the server to manage the messages to interchange with the components.
 The management web interface can be open at **http://localhost:8081** with the credential
 **mov**:**password**.
 * __MongoDB__  the database to store the data used by the MOV. The database is named as **movDB** and the user credentials **mov:password**.
 The management web interface can be open at **http://localhost:8081** with the credential
 **mov**:**password**.
 * __Mongo express__  the web interface to interact with the mongoDB. The web interface
  can be open at **http://localhost:8082**.


# Deploy

You can build the docker image to deploy this component with the script:

```shell script
./buildDockerImages.sh
```

As a parameter you can pass the name for the tag. If not it use the current version.

