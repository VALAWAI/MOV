/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import java.util.Properties;
import java.util.concurrent.Flow.Subscriber;

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.eclipse.microprofile.reactive.messaging.spi.ConnectorFactory;

import io.quarkus.logging.Log;
import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.rabbitmq.RabbitMQConnector;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The components to publish messages.
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
	 * Publish the specified payload.
	 *
	 * @param channelName name of the channel to publish a message.
	 * @param payload     of the message to publish.
	 *
	 * @return {@code true} if the payload has been sent.
	 */
	public <P extends Payload> boolean send(String channelName, P payload) {

		try {

			final var properties = new Properties();
			properties.put(ConnectorFactory.CHANNEL_NAME_ATTRIBUTE, channelName);
			properties.put("default-routing-key", channelName);
			properties.put("exchange.name", "\"\"");
			properties.put("content_type", "application/json");

			final var builder = ConfigProviderResolver.instance().getBuilder();
			builder.withSources(new PropertiesConfigSource(properties, ""));
			final var config = builder.build();

			@SuppressWarnings("unchecked")
			final var publisher = (Subscriber<Message<P>>) this.connector.getSubscriber(config);
			Multi.createFrom().item(payload).map(Message::of).subscribe(publisher);
			return true;

		} catch (final Throwable error) {

			Log.errorv(error, "Cannot send to {0} the {1}", channelName, payload);
			return false;
		}

	}

}
