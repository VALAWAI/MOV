/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import com.rabbitmq.client.AMQP.BasicProperties;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.buffer.Buffer;
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
	 * The Rabbit MQ service.
	 */
	@Inject
	RabbitMQService service;

	/**
	 * Publish the specified JSON payload.
	 *
	 * @param queueName name of the queue to publish a message.
	 * @param payload   of the message to publish.
	 *
	 * @return {@code true} if the payload has been sent.
	 */
	public Uni<Void> send(String queueName, Object payload) {

		if (payload instanceof final JsonObject json) {

			return this.sendJson(queueName, json);

		} else {

			try {

				final var json = Json.encodeToBuffer(payload).toJsonObject();
				return this.sendJson(queueName, json);

			} catch (final Throwable error) {

				return Uni.createFrom().failure(error);
			}
		}
	}

	/**
	 * Publish the specified JSON payload.
	 *
	 * @param queueName name of the queue to publish a message.
	 * @param payload   of the message to publish.
	 *
	 * @return {@code true} if the payload has been sent.
	 */
	public Uni<Void> sendJson(String queueName, JsonObject payload) {

		try {

			final var buffer = new Buffer(Json.encodeToBuffer(payload));
			return this.service.client().chain(client -> {

				final var properties = new BasicProperties.Builder().contentType("application/json").build();
				return client.basicPublish("", queueName, properties, buffer);

			});

		} catch (final Throwable error) {

			return Uni.createFrom().failure(error);
		}

	}

}
