/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.eclipse.microprofile.reactive.messaging.spi.ConnectorFactory;

import eu.valawai.mov.persistence.logs.AddLog;
import io.quarkus.logging.Log;
import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.reactive.messaging.rabbitmq.RabbitMQConnector;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The element used to manage the changes on the topology.
 *
 * @see ChangeTopologyPayload
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class ChangeTopologyManager {

	/**
	 * The component to manage the messages.
	 */
	@Inject
	PayloadService service;

	/**
	 * Client to connect with the RabbitMQ.
	 */
	@Inject
	@Connector(RabbitMQConnector.CONNECTOR_NAME)
	RabbitMQConnector connector;

	/**
	 * The current topology connections.
	 */
	protected final List<TopologyConnectionManager> managers = new ArrayList<>();

	/**
	 * Called when has to register a component.
	 *
	 * @param content of the message to consume.
	 */
	@Incoming("change_topology")
	public void consume(JsonObject content) {

		final var payload = this.service.decodeAndVerify(content, ChangeTopologyPayload.class);
		if (payload == null) {

			AddLog.fresh().withError().withMessage("Received invalid change topology payload.").withPayload(content)
					.store();

		} else {
			// do something
			final var top = new TopologyConnectionManager();
			top.setTarget(payload.target);
			top.setSource(payload.source);
			this.managers.add(top);
		}

	}

	/**
	 * The manager of a topology connection.
	 */
	protected class TopologyConnectionManager implements Subscriber<Message<?>> {

		/**
		 * The source channel.
		 */
		protected String source;

		/**
		 * The target channel.
		 */
		protected String target;

		/**
		 * The subscription.
		 */
		Subscription subscription;

		/**
		 * The component to publish messages.
		 */
		Flow.Subscriber<Message<?>> publisher;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onSubscribe(Subscription subscription) {

			Log.debugv("Subscriber to receive messages from {0}", this.source);
			this.subscription = subscription;
			subscription.request(1);

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onNext(Message<?> item) {

			try {

				final var body = item.getPayload();
				this.publisher.onNext(item);
				Log.debugv("Received from {0} the payload {1} and sent to {2}.", this.source, body, this.target);
				this.subscription.request(1);

			} catch (final Throwable error) {

				Log.errorv(error, "Cannot manage a message received from {0}.", this.source);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onError(Throwable error) {

			Log.errorv(error, "Error when manage the channel {0}.", this.source);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onComplete() {

			Log.debugv("Finished to manage the channel {0}.", this.source);
		}

		/**
		 * Listen for the messages that are received by a channel.
		 *
		 * @param source name of the channel to listen.
		 */
		public void setSource(String source) {

			try {

				final var properties = new Properties();
				properties.put(ConnectorFactory.CHANNEL_NAME_ATTRIBUTE, source);

				final var builder = ConfigProviderResolver.instance().getBuilder();
				builder.withSources(new PropertiesConfigSource(properties, ""));
				final var config = builder.build();
				final var publisher = ChangeTopologyManager.this.connector.getPublisher(config);
				publisher.subscribe(this);

			} catch (final Throwable error) {

				Log.errorv(error, "Cannot open the RabbitMQ channel.");
			}
		}

		/**
		 * Create the component to publish messages on the target channel.
		 *
		 * @param target channel to publish messages.
		 */
		@SuppressWarnings("unchecked")
		public void setTarget(String target) {

			try {

				final var properties = new Properties();
				properties.put(ConnectorFactory.CHANNEL_NAME_ATTRIBUTE, target);

				final var builder = ConfigProviderResolver.instance().getBuilder();
				builder.withSources(new PropertiesConfigSource(properties, ""));
				final var config = builder.build();

				this.publisher = (Subscriber<Message<?>>) ChangeTopologyManager.this.connector.getSubscriber(config);
				final var subscription = new Flow.Subscription() {

					@Override
					public void request(long n) {
					}

					@Override
					public void cancel() {
					}
				};
				this.publisher.onSubscribe(subscription);

			} catch (final Throwable error) {

				Log.errorv(error, "Cannot open the RabbitMQ channel.");
			}
		}

	}

}
