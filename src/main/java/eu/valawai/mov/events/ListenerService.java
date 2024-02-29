/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.eclipse.microprofile.reactive.messaging.spi.ConnectorFactory;

import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.rabbitmq.RabbitMQConnector;
import io.smallrye.reactive.messaging.rabbitmq.RabbitMQConnectorIncomingConfiguration;
import io.smallrye.reactive.messaging.rabbitmq.internals.IncomingRabbitMQChannel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The service used to listener for messages that comes from the broker.
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class ListenerService {

	/**
	 * Client to connect with the RabbitMQ.
	 */
	@Inject
	@Connector(RabbitMQConnector.CONNECTOR_NAME)
	RabbitMQConnector connector;

	/**
	 * The active listeners.
	 */
	protected volatile List<ActiveListener> listeners = Collections.synchronizedList(new ArrayList<>());

	/**
	 * Publish the specified payload.
	 *
	 * @param channelName name of the channel to lister for messages.
	 *
	 * @return the messages that are received by the channel.
	 */
	public Multi<? extends Message<?>> open(String channelName) {

		if (channelName == null) {

			return Multi.createFrom().failure(() -> new IllegalArgumentException("The queue name cannot be null"));

		} else {

			synchronized (this.listeners) {

				for (final var listener : this.listeners) {

					if (listener.queueName.equals(channelName)) {
						// already defined
						return null;
					}
				}

				try {

					final var listener = new ActiveListener(channelName);
					this.listeners.add(listener);
					return listener.incoming.getStream();

				} catch (final Throwable error) {

					return Multi.createFrom().failure(error);
				}
			}
		}
	}

	/**
	 * Close a channel.
	 *
	 * @param channelName name of the channel to close.
	 *
	 * @return {@code true} if the channel is closed or {@code false} otherwise}.
	 */
	public boolean close(String channelName) {

		if (channelName != null) {

			synchronized (this.listeners) {

				for (final var subscriber : this.listeners) {

					if (subscriber.queueName.equals(channelName)) {
						// already defined
						subscriber.incoming.terminate();
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * The clients to manage the messages that comes from the broker.
	 */
	protected class ActiveListener {

		/**
		 * The name of the queue that the messages come from.
		 */
		public String queueName;

		/**
		 * The channel with the broker.
		 */
		public IncomingRabbitMQChannel incoming;

		/**
		 * Create the component to receive the messages of a specific queue name.
		 *
		 * @param queueName name of the channel to lister for messages.
		 */
		public ActiveListener(String queueName) {

			this.queueName = queueName;
			final var properties = new Properties();
			properties.put(ConnectorFactory.CHANNEL_NAME_ATTRIBUTE, queueName);
			properties.put("queue.name", queueName);
			properties.put("content_type", "application/json");

			final var builder = ConfigProviderResolver.instance().getBuilder();
			builder.withSources(new PropertiesConfigSource(properties, ""));
			final var config = builder.build();

			final RabbitMQConnectorIncomingConfiguration ic = new RabbitMQConnectorIncomingConfiguration(config);
			this.incoming = new IncomingRabbitMQChannel(ListenerService.this.connector, ic);

		}
	}

}
