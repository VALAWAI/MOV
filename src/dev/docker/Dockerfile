FROM openjdk:21-jdk-slim-bookworm

RUN apt-get update && apt-get install -y \
    curl \
    git \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs \
    && rm -rf /var/lib/apt/lists/*

RUN npm install -g @angular/cli@latest

RUN curl -Ls https://sh.jbang.dev | bash -s - trust add https://repo1.maven.org/maven2/io/quarkus/quarkus-cli/ && \
    curl -Ls https://sh.jbang.dev | bash -s - app install --fresh --force quarkus@quarkusio
	
WORKDIR /root/.redhat

RUN echo "{\"disabled\":true}" > /root/.redhat/io.quarkus.analytics.localconfig

WORKDIR /root

RUN echo "PS1='\[\033[01;32m\]mov@dev\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\] \$ '" >> /root/.bashrc
RUN echo "alias mvn=\"/app/mvnw -Dstyle.color=always -Dquarkus-log-max-startup-records=100000\"" >> /root/.bashrc
RUN echo "alias mvnd=\"mvn -Dmaven.surefire.debug=\\\"-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=0.0.0.0:5005 -Xnoagent -Djava.compiler=NONE\\\"\"" >> /root/.bashrc
RUN echo "alias startServer=\"mvn clean compile quarkus:dev -DdebugHost=0.0.0.0\"" >> /root/.bashrc
RUN echo "/app/checkUpdate.sh" >> /root/.bashrc

WORKDIR /app