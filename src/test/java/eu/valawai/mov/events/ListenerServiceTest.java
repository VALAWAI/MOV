/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.ValueGenerator;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;

/**
 * Test the {@link ListenerService}.
 *
 * @see ListenerService
 *
 * @author VALAWAI
 */
@QuarkusTest
public class ListenerServiceTest extends MovEventTestCase {

	/**
	 * The service to test.
	 */
	@Inject
	ListenerService service;

	/**
	 * The service to test.
	 */
	@Inject
	@Channel("listener_service_test")
	Emitter<JsonObject> emitter;

	/**
	 * The service to publish messages.
	 */
	@Inject
	PublishService publisher;

	/**
	 * The default name of the channel to send events.
	 */
	@ConfigProperty(name = "mp.messaging.outgoing.listener_service_test.exchange.name", defaultValue = "test/listener_service")
	String channelName;

	/**
	 * Check not create open with {@code null} queue name.
	 */
	@Test
	public void shouldNotOpenWithNullQueueName() {

		final var errors = new ArrayList<Throwable>();
		final var msg = this.service.open(null).onFailure().recoverWithItem(error -> {

			errors.add(error);
			return null;

		}).collect().first().await().atMost(Duration.ofSeconds(30));
		assertNull(msg);
		assertEquals(1, errors.size());
		assertInstanceOf(IllegalArgumentException.class, errors.get(0));

	}

	/**
	 * Check not create closer with {@code null} queue name.
	 */
	@Test
	public void shouldNotCloseWithNullQueueName() {

		assertFalse(this.service.close(null));

	}

	/**
	 * Check listen messages from publisher.
	 */
	@Test
	public void shouldReceiveFromPublisher() {

		final var semaphore = new Semaphore(0);
		final var messages = Collections.synchronizedList(new ArrayList<Message<?>>());
		final var queueName = ValueGenerator.nextPattern("queue_name_{0}");
		this.service.open(queueName).subscribe().with(msg -> {

			messages.add(msg);
			semaphore.release();

		}, error -> {

			semaphore.release();
		});
		final var payload = new JsonObject();
		payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
		this.publisher.send(queueName, payload);

		try {

			semaphore.tryAcquire(1, TimeUnit.MINUTES);

		} catch (final InterruptedException ignored) {
		}

		assertTrue(this.service.close(queueName));
		assertEquals(1, messages.size());
		assertEquals(payload, messages.get(0).getPayload());

	}

	/**
	 * Check listen messages from emitter.
	 */
	@Test
	public void shouldReceiveFromEmitter() {

		final var semaphore = new Semaphore(0);
		final var messages = Collections.synchronizedList(new ArrayList<Message<?>>());
		this.service.open(this.channelName).subscribe().with(msg -> {

			messages.add(msg);
			semaphore.release();

		}, error -> {

			semaphore.release();
		});
		final var payload = new JsonObject();
		payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
		this.emitter.send(payload);

		try {

			semaphore.tryAcquire(1, TimeUnit.MINUTES);

		} catch (final InterruptedException ignored) {
		}

		assertTrue(this.service.close(this.channelName));
		assertEquals(1, messages.size());
		assertEquals(payload, messages.get(0).getPayload());

	}

}
