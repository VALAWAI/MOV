/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.ValueGenerator;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;

/**
 * Test the {@link PublishService}.
 *
 * @see PublishService
 *
 * @author VALAWAI
 */
@QuarkusTest
public class PublishServiceTest extends MovEventTestCase {

	/**
	 * The service to test.
	 */
	@Inject
	PublishService service;

	/**
	 * The component to listen for messages.
	 */
	@Inject
	ListenerService listener;

	/**
	 * The default name of the channel to send events.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.publisher_service_test.queue.name", defaultValue = "test/publisher_service")
	String channelName;

	/**
	 * Check not send {@code null} object.
	 */
	@Test
	public void shouldNotSendNullValue() {

		var error = this.assertFailure(this.service.send(null, null));
		assertInstanceOf(DecodeException.class, error);

		error = this.assertFailure(this.service.send(null, "payload"));
		assertInstanceOf(DecodeException.class, error);

		error = this.assertFailure(this.service.send("queue_name", null));
		assertInstanceOf(DecodeException.class, error);
	}

	/**
	 * Check send messages that are captured by a listener.
	 *
	 * @throws InterruptedException if a waiting time fails.
	 */
	@Test
	public void shouldSendToListener() throws InterruptedException {

		final var semaphore = new Semaphore(0);
		final var messages = Collections.synchronizedList(new ArrayList<JsonObject>());
		final var queueName = ValueGenerator.nextPattern("exchange_name_{0}");

		this.listener.open(queueName).subscribe().with(payload -> {

			messages.add(payload);
			semaphore.release();

		}, error -> {

			semaphore.release();
		});
		try {

			final var payload = new JsonObject();
			payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
			this.assertItemIsNull(this.service.send(queueName, payload));
			semaphore.tryAcquire(1, TimeUnit.MINUTES);

			assertEquals(1, messages.size());
			assertEquals(payload, messages.get(0));

		} finally {

			this.assertItemIsNull(this.listener.close(queueName));
		}
	}

	/**
	 * Check send a lot of messages that are captured by a listener.
	 *
	 * @throws InterruptedException if a waiting time fails.
	 */
	@Test
	public void shouldSendAlotToListener() throws InterruptedException {

		final var semaphore = new Semaphore(0);
		final var receivedPayloads = Collections.synchronizedList(new ArrayList<JsonObject>());
		final var queueName = ValueGenerator.nextPattern("exchange_name_{0}");

		this.listener.open(queueName).subscribe().with(payload -> {

			receivedPayloads.add(payload);
			semaphore.release();

		}, error -> {

			semaphore.release();
		});
		try {

			final var expectedPayloads = new ArrayList<JsonObject>();
			final var max = 100;
			for (var i = 0; i < max; i++) {

				final var payload = new JsonObject();
				payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
				expectedPayloads.add(payload);
				this.assertItemIsNull(this.service.send(this.channelName, payload));

			}

			semaphore.tryAcquire(max, TimeUnit.MINUTES);

			assertEquals(max, receivedPayloads.size());

			for (final var payload : receivedPayloads) {

				assertTrue(expectedPayloads.remove(payload));
			}

		} finally {

			this.assertItemIsNull(this.listener.close(queueName));
		}

	}

	/**
	 * Check send a message that is captured by an incoming.
	 */
	@Test
	public void shouldSendToIncoming() {

		PublisherConsumer.clear();
		final var payload = new JsonObject();
		payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
		this.assertItemIsNull(this.service.send(this.channelName, payload));

		final var received = PublisherConsumer.waitForPayload(Duration.ofMinutes(1));
		assertNotNull(received);
		assertEquals(payload, received);

	}

	/**
	 * Check send a lot of messages that are captured by an incoming.
	 */
	@Test
	public void shouldSendAlotToIncoming() {

		PublisherConsumer.clear();
		final var expectedPayloads = new ArrayList<JsonObject>();
		for (var i = 0; i < 100; i++) {

			final var payload = new JsonObject();
			payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
			expectedPayloads.add(payload);
			this.assertItemIsNull(this.service.send(this.channelName, payload));

		}
		do {

			final var received = PublisherConsumer.waitForPayload(Duration.ofMinutes(1));
			assertNotNull(received);
			assertTrue(expectedPayloads.remove(received));

		} while (!expectedPayloads.isEmpty());

	}

}
