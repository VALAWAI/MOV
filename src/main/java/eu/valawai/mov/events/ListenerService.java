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
	List<RabbitMQConsumer> consumers = Collections.synchronizedList(new ArrayList<>());

	/**
	 * Publish the specified payload.
	 *
	 * @param queueName name of the queue to lister for messages.
	 *
	 * @return the payload that are received by the queue.
	 */
	public Multi<JsonObject> open(String queueName) {

		if (queueName == null) {

			return Multi.createFrom().failure(new IllegalArgumentException("The name of the queue can not be null"));

		} else {

			synchronized (this.consumers) {

				for (final var consumer : this.consumers) {

					if (queueName.equals(consumer.queueName())) {

						return Multi.createFrom().failure(new IllegalArgumentException("The queue is already opened"));

					}
				}
				final var options = new QueueOptions();
				options.setAutoAck(true);
				options.setConsumerExclusive(false);
				options.setConsumerTag(this.getClass().getName() + "#" + queueName);
				return this.service.client().chain(client -> {

					return client.queueDeclare(queueName, true, false, false).map(any -> {

						return client;
					});

				}).chain(client -> client.basicConsumer(queueName, options)).onItem().transformToMulti(consumer -> {

					this.consumers.add(consumer);
					return consumer.toMulti().map(msg -> {

						final var body = msg.body();
						return body.toJsonObject();
					});
				});
			}
		}
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

					}
				}
			}
		}

		return Uni.createFrom().failure(
				new IllegalArgumentException("Does not exist an open queue associated with the specified name."));
	}

}
