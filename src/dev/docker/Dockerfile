# syntax=docker/dockerfile:experimental
FROM node:20.10.0-bookworm-slim

USER root

SHELL ["/bin/bash", "-c"]
RUN apt-get update && \
    apt-get -qy full-upgrade && \
    apt-get -qq -y install curl openjdk-17-jdk zip unzip && \
    curl -sSL https://get.docker.com/ | sh

RUN npm install -g npm @angular/cli

RUN curl -s "https://get.sdkman.io" | bash && \
	chmod a+x "$HOME/.sdkman/bin/sdkman-init.sh" && \
	source "$HOME/.sdkman/bin/sdkman-init.sh" && \
	sdk install quarkus

WORKDIR /app

RUN echo "PS1='\[\033[01;32m\]mov@dev\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\] \$ '" >> /root/.bashrc
RUN echo "alias mvn=\"/app/mvnw -Dstyle.color=always -Dquarkus-log-max-startup-records=100000\"" >> /root/.bashrc
RUN echo "alias mvnd=\"mvn -Dmaven.surefire.debug=\\\"-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=0.0.0.0:5005 -Xnoagent -Djava.compiler=NONE\\\"\"" >> /root/.bashrc
RUN echo "alias startServer=\"mvn compile quarkus:dev -DdebugHost=0.0.0.0\"" >> /root/.bashrc
RUN echo "export JAVA_HOME=\$(readlink -f /usr/bin/javac | sed \"s:/bin/javac::\")" >> /root/.bashrc
RUN echo "/app/checkUpdate.sh" >> /root/.bashrc
