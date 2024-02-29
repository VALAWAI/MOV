/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.eclipse.microprofile.reactive.messaging.spi.ConnectorFactory;

import io.quarkus.logging.Log;
import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.rabbitmq.RabbitMQConnector;
import io.smallrye.reactive.messaging.rabbitmq.RabbitMQConnectorOutgoingConfiguration;
import io.smallrye.reactive.messaging.rabbitmq.internals.OutgoingRabbitMQChannel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The service used to publish messages to the broker.
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class PublishService {

	/**
	 * Client to connect with the RabbitMQ.
	 */
	@Inject
	@Connector(RabbitMQConnector.CONNECTOR_NAME)
	RabbitMQConnector connector;

	/**
	 * The component to execute the send process.
	 */
	ExecutorService executor = new ScheduledThreadPoolExecutor(4);

	/**
	 * Publish the specified payload.
	 *
	 * @param channelName name of the channel to publish a message.
	 * @param payload     of the message to publish.
	 *
	 * @return {@code true} if the payload has been sent.
	 */
	public <P> boolean send(String channelName, P payload) {

		if (channelName == null || payload == null) {

			return false;

		} else {

			this.executor.execute(() -> this.sendProcess(channelName, payload));
			return true;
		}

	}

	/**
	 * Process to send the payload.
	 *
	 * @param channelName name of the channel to publish a message.
	 * @param payload     of the message to publish.
	 */
	private void sendProcess(String channelName, Object payload) {

		try {

			final var properties = new Properties();
			properties.put(ConnectorFactory.CHANNEL_NAME_ATTRIBUTE, channelName);
			properties.put("default-routing-key", channelName);
			properties.put("exchange.name", "\"\"");
			properties.put("content_type", "application/json");

			final var builder = ConfigProviderResolver.instance().getBuilder();
			builder.withSources(new PropertiesConfigSource(properties, ""));
			final var config = builder.build();

			final RabbitMQConnectorOutgoingConfiguration oc = new RabbitMQConnectorOutgoingConfiguration(config);
			final var outgoing = new OutgoingRabbitMQChannel(this.connector, oc);

			final var subscriber = outgoing.getSubscriber();
			Multi.createFrom().item(Message.of(payload)).subscribe(subscriber);

		} catch (final Throwable error) {

			Log.errorv(error, "Cannot send {0} to {1}", payload, channelName);
		}

	}

}
