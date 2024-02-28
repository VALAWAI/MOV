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
	 * Service to send messages to the message broker.
	 */
	@Inject
	PublishService publish;

	/**
	 * The current topology connections.
	 */
	protected volatile List<TopologyConnectionManager> managers = Collections.synchronizedList(new ArrayList<>());

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
//			final var top = new TopologyConnectionManager();
//			synchronized (managers) {
//
//				for(var manager : this.managers) {
//
//					if( manager.source.equals(payload.source) ) {
//
//						if( payload.action == TopologyAction.DISABLE) {
//
//						}
//						//else  It is already enabled => nothing to do
//						return;
//					}
//				}
//
//			}
//			top.setTarget(payload.target);
//			top.setSource();
//			this.managers.add(top);
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
		 * The subscription.
		 */
		Subscription subscription;

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
//				ChangeTopologyManager.this.publish.sendMessage(this.target, item);
//				Log.debugv("Received from {0} the payload {1} and sent to {2}.", this.source, body, this.target);
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

				this.source = source;
				final var properties = new Properties();
				properties.put(ConnectorFactory.CHANNEL_NAME_ATTRIBUTE, source);
				properties.put("queue.name", source);
				properties.put("content_type", "application/json");

				final var builder = ConfigProviderResolver.instance().getBuilder();
				builder.withSources(new PropertiesConfigSource(properties, ""));
				final var config = builder.build();
				final var publisher = ChangeTopologyManager.this.connector.getPublisher(config);
				publisher.subscribe(this);

			} catch (final Throwable error) {

				Log.errorv(error, "Cannot open the RabbitMQ channel.");
			}
		}

	}

}
