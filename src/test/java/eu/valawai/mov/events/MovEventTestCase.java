/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import static org.junit.jupiter.api.Assertions.fail;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;

import eu.valawai.mov.MasterOfValawaiTestCase;
import io.quarkus.logging.Log;
import io.quarkus.test.common.QuarkusTestResource;
import io.vertx.core.json.Json;

/**
 * Generic test over the components that manage an event.
 *
 * @author VALAWAI
 */
@QuarkusTestResource(RabbitMQTestResource.class)
public class MovEventTestCase extends MasterOfValawaiTestCase {

	/**
	 * The raabitMQ host.
	 */
	@ConfigProperty(name = "rabbitmq-host", defaultValue = "host.docker.internal")
	String rabbitmqHost;

	/**
	 * The raabitMQ port.
	 */
	@ConfigProperty(name = "rabbitmq-port", defaultValue = "5672")
	int rabbitmqPort;

	/**
	 * Check that publish the specified payload.
	 *
	 * @param channelName name of the channel to publish a message.
	 * @param payload     of the message to publish.
	 */
	protected <P> void assertPublish(String channelName, P payload) {

		final ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(this.rabbitmqHost);
		factory.setPort(this.rabbitmqPort);
		factory.setUsername("valawai");
		factory.setPassword("password");
		try (var connection = factory.newConnection(); var channel = connection.createChannel()) {

			final var encoded = Json.encode(payload);
			final var bytes = encoded.getBytes();
			final var msg = new AMQP.BasicProperties.Builder().contentType("application/json").build();
			channel.basicPublish("", channelName, msg, bytes);

		} catch (final Throwable error) {

			Log.errorv(error, "Cannot send to {0} the {1}", channelName, payload);
			fail("Cannot publish message.");
		}

	}

}
