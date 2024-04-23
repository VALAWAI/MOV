/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * The resource that starts a RabbitMQ container to use on the tests.
 *
 * @author VALAWAI
 */
public class RabbitMQTestResource implements QuarkusTestResourceLifecycleManager {

	/**
	 * The name of the rabbit mq docker image to use.
	 */
	public static final String RABBITMQ_DOCKER_NAME = "rabbitmq:latest";

	/**
	 * The RabbitMQ service container.
	 */
	static GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(RABBITMQ_DOCKER_NAME))
			.withStartupAttempts(1).withEnv("RABBITMQ_DEFAULT_USER", "mov").withEnv("RABBITMQ_DEFAULT_PASS", "password")
			.withExposedPorts(5672).waitingFor(Wait.forListeningPort());

	/**
	 * Start the mocked server.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> start() {

		final var conf = new HashMap<String, String>();
		if (Boolean.parseBoolean(System.getProperty("useDevMQ"))) {

			conf.put("rabbitmq-host", "host.docker.internal");

		} else {

			container.start();
			conf.put("rabbitmq-host", container.getHost());
			conf.put("rabbitmq-port", String.valueOf(container.getMappedPort(5672)));
		}

		return conf;

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
