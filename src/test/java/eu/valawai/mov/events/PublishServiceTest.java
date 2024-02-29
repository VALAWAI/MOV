/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.ValueGenerator;
import io.quarkus.test.junit.QuarkusTest;
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

		assertFalse(this.service.send(null, null));
		assertFalse(this.service.send(null, "payload"));
		assertFalse(this.service.send("queue_name", null));
	}

	/**
	 * Check send messages that are captured by a listener.
	 */
	@Test
	public void shouldSendToListener() {

		final var semaphore = new Semaphore(0);
		final var messages = Collections.synchronizedList(new ArrayList<Message<?>>());
		final var queueName = ValueGenerator.nextPattern("exchange_name_{0}");
		this.listener.open(queueName).subscribe().with(msg -> {

			messages.add(msg);
			semaphore.release();

		}, error -> {

			semaphore.release();
		});
		final var payload = new JsonObject();
		payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
		this.service.send(queueName, payload);

		try {

			semaphore.tryAcquire(1, TimeUnit.MINUTES);

		} catch (final InterruptedException ignored) {
		}

		assertTrue(this.listener.close(queueName));
		assertEquals(1, messages.size());
		assertEquals(payload, messages.get(0).getPayload());

	}

	/**
	 * Check send messages that are captured by an incoming.
	 */
	@Test
	public void shouldSendToIncomming() {

		PublisherConsumer.clear();
		final var payload = new JsonObject();
		payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
		this.service.send(this.channelName, payload);

		final var received = PublisherConsumer.waitForPayload(Duration.ofMinutes(1));
		assertNotNull(received);
		assertEquals(payload, received);

	}

}
