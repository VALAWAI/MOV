/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.rabbitmq.RabbitMQConsumer;
import io.vertx.rabbitmq.QueueOptions;
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
	 * The Rabbit MQ service.
	 */
	@Inject
	RabbitMQService service;

	/**
	 * The open connections.
	 */
	private final List<RabbitMQConsumer> consumers = Collections.synchronizedList(new ArrayList<>());

	/**
	 * The connections that try to open.
	 */
	private final Set<String> starting = Collections.synchronizedSet(new HashSet<>());

	/**
	 * Open a component to listen for the messages of a queue.
	 *
	 * @param queueName name of the queue to lister for messages.
	 *
	 * @return the body if the messages received by the queue.
	 */
	public Multi<JsonObject> open(String queueName) {

		return this.toMultiBody(this.openConsumer(queueName));
	}

	/**
	 * Convert a consumer to a listener.
	 *
	 * @param open to process to create the consumer.
	 *
	 * @return the body if the messages received by the consumer.
	 */
	public Multi<JsonObject> toMultiBody(Uni<RabbitMQConsumer> open) {

		return open.onItem().transformToMulti(consumer -> {

			return consumer.toMulti().map(msg -> {

				final var body = msg.body();
				return body.toJsonObject();
			});
		});
	}

	/**
	 * Open a component to consume for the messages of a queue.
	 *
	 * @param queueName name of the queue to consume for messages.
	 *
	 * @return the consumer for the queue messages.
	 */
	public Uni<RabbitMQConsumer> openConsumer(String queueName) {

		if (queueName == null) {

			return Uni.createFrom()
					.failure(() -> new IllegalArgumentException("The name of the queue can not be null"));

		} else {

			synchronized (this.consumers) {

				if (!this.starting.add(queueName)) {

					return Uni.createFrom().failure(() -> new IllegalArgumentException("The queue is starting"));

				} else {

					for (final var consumer : this.consumers) {

						if (queueName.equals(consumer.queueName())) {

							return Uni.createFrom()
									.failure(() -> new IllegalArgumentException("The queue is already opened"));

						}
					}
					final var options = new QueueOptions();
					options.setAutoAck(true);
					options.setConsumerExclusive(false);
					options.setKeepMostRecent(true);
					options.setConsumerTag(this.getClass().getName() + "#" + queueName);
					return this.service.client().chain(client -> {

						return client.queueDeclare(queueName, true, false, false).map(any -> {

							return client;
						});

					}).chain(client -> {

						return client.basicConsumer(queueName, options);

					}).onFailure().invoke(any -> {

						this.starting.remove(queueName);

					}).onItem().invoke(consumer -> {

						this.consumers.add(consumer);
						this.starting.remove(queueName);
					});
				}
			}
		}
	}

	/**
	 * Check if the queue is open.
	 *
	 * @param queueName name of the queue to check.
	 *
	 * @return {@code true} if the queue is open.
	 */
	public boolean isOpen(String queueName) {

		if (queueName != null) {

			for (final var consumer : this.consumers) {

				if (queueName.equals(consumer.queueName())) {

					return true;

				}
			}
		}

		return false;
	}

	/**
	 * Close a queue.
	 *
	 * @param queueName name of the queue to close.
	 *
	 * @return an empty result if the queue is closed or an error that explain why
	 *         cannot close the queue.
	 */
	public Uni<Void> close(String queueName) {

		if (queueName != null) {

			synchronized (this.consumers) {

				final var max = this.consumers.size();
				for (var i = 0; i < max; i++) {

					final var consumer = this.consumers.get(i);
					if (queueName.equals(consumer.queueName())) {

						this.consumers.remove(i);
						return consumer.cancel();
//.chain(any -> this.service.client()).chain(client -> client.queueDeleteIf(queueName, true, false).map(any -> null))
					}
				}
			}
		}

		return Uni.createFrom().failure(
				() -> new IllegalArgumentException("Does not exist an open queue associated with the specified name."));
	}

}
