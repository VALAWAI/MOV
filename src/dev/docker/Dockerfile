FROM amazoncorretto:25-alpine-jdk
RUN apk add --no-cache \
    bash \
    curl \
    git \
    build-base \
    nodejs \
    unzip \
    zip \
    docker \
    npm \
	&& rm -rf /var/cache/apk/*
SHELL ["/bin/bash", "-c"]

RUN curl -s "https://get.sdkman.io" | bash 
RUN source "/root/.sdkman/bin/sdkman-init.sh"  && sdk selfupdate force && sdk install quarkus

RUN npm install -g @angular/cli@latest

WORKDIR /root/.redhat

RUN echo "{\"disabled\":true}" > /root/.redhat/io.quarkus.analytics.localconfig

WORKDIR /root

RUN echo "PS1='\[\033[01;32m\]mov@dev\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\] \$ '" >> /root/.bashrc
RUN echo "alias mvn=\"/app/mvnw -Dstyle.color=always -Dquarkus-log-max-startup-records=100000\"" >> /root/.bashrc
RUN echo "alias mvnd=\"mvn -Dmaven.surefire.debug=\\\"-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=0.0.0.0:5005 -Djava.compiler=NONE\\\"\"" >> /root/.bashrc
RUN echo "alias startServer=\"mvn clean compile quarkus:dev -DdebugHost=0.0.0.0\"" >> /root/.bashrc
RUN echo "/app/checkUpdate.sh" >> /root/.bashrc

WORKDIR /app