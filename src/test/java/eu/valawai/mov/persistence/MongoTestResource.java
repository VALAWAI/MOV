/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/
package eu.valawai.mov.persistence;

import java.util.Collections;
import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * The resource that start a docker container with a Mongo database.
 *
 * @author VALAWAI
 */
public class MongoTestResource implements QuarkusTestResourceLifecycleManager {

	/**
	 * The name of the mongo docker container to use.
	 */
	public static final String MONGO_DOCKER_NAME = "mongo:6.0.13";

	/**
	 * The mongo service container.
	 */
	@SuppressWarnings("resource")
	static GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(MONGO_DOCKER_NAME))
			.withStartupAttempts(1).withEnv("MONGO_INITDB_ROOT_USERNAME", "root")
			.withEnv("MONGO_INITDB_ROOT_PASSWORD", "password").withEnv("MONGO_INITDB_DATABASE", "movDB")
			.withCopyFileToContainer(
					MountableFile.forClasspathResource(
							MongoTestResource.class.getPackageName().replaceAll("\\.", "/") + "/initialize-movDB.js"),
					"/docker-entrypoint-initdb.d/init-mongo.js")
			.withExposedPorts(27017).waitingFor(Wait.forListeningPort());

	/**
	 * Start the mocked server.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> start() {

		if (Boolean.parseBoolean(System.getProperty("useDevDatabase"))) {

			return Collections.singletonMap("quarkus.mongodb.connection-string",
					"mongodb://mov:password@host.docker.internal:27017/movDB");

		} else {

			container.start();
			return Collections.singletonMap("quarkus.mongodb.connection-string",
					"mongodb://mov:password@" + container.getHost() + ":" + container.getMappedPort(27017) + "/movDB");

		}

	}

	/**
	 * Stop the mocked server.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {

		if (container.isRunning()) {

			container.close();

		}
	}

}
