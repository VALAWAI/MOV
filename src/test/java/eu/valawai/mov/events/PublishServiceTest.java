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
	TestQueue testQueue;

	/**
	 * Check not send {@code null} object.
	 */
	@Test
	public void shouldNotSendNullValue() {

		var error = this.assertFailure(this.publish.send(null, null));
		assertInstanceOf(DecodeException.class, error);

		error = this.assertFailure(this.publish.send(null, "payload"));
		assertInstanceOf(DecodeException.class, error);

		error = this.assertFailure(this.publish.send("queue_name", null));
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
		final var queueName = ValueGenerator.nextPattern("queue_name_{0}");

		this.listener.open(queueName).subscribe().with(payload -> {

			messages.add(payload);
			semaphore.release();

		}, error -> {

			error.printStackTrace();
			semaphore.release();
		});
		this.waitUntilQueueIsOpen(queueName);

		final var payload = new JsonObject();
		payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
		this.assertPublish(queueName, payload);

		semaphore.tryAcquire(30, TimeUnit.SECONDS);

		assertEquals(1, messages.size());
		assertEquals(payload, messages.get(0));
	}

	/**
	 * Check send a lot of messages that are captured by a listener.
	 *
	 * @throws InterruptedException if a waiting time fails.
	 */
	@Test
	public void shouldSendSomeMessagesToAListener() throws InterruptedException {

		final var semaphore = new Semaphore(0);
		final var receivedPayloads = Collections.synchronizedList(new ArrayList<JsonObject>());
		final var queueName = ValueGenerator.nextPattern("queue_name_{0}");

		this.listener.open(queueName).subscribe().with(payload -> {

			receivedPayloads.add(payload);
			semaphore.release();

		}, error -> {

			error.printStackTrace();
			semaphore.release();
		});
		this.waitUntilQueueIsOpen(queueName);

		final var expectedPayloads = new ArrayList<JsonObject>();
		for (var i = 0; i < 100; i++) {

			final var payload = new JsonObject();
			payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
			expectedPayloads.add(payload);
			this.assertPublish(queueName, payload);

		}

		semaphore.tryAcquire(expectedPayloads.size(), 1, TimeUnit.MINUTES);

		assertEquals(expectedPayloads.size(), receivedPayloads.size());

		for (final var payload : receivedPayloads) {

			assertTrue(expectedPayloads.remove(payload));
		}

	}

	/**
	 * Check send a message that is captured by an incoming.
	 */
	@Test
	public void shouldSendToIncoming() {

		TestQueue.clear();
		final var payload = new JsonObject();
		payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
		this.assertItemIsNull(this.publish.send(this.testQueue.getInputQueueName(), payload));

		final var received = TestQueue.waitForPayload();
		assertNotNull(received);
		assertEquals(payload, received);

	}

	/**
	 * Check send a lot of messages that are captured by an incoming.
	 */
	@Test
	public void shouldSendAlotToIncoming() {

		TestQueue.clear();
		final var expectedPayloads = new ArrayList<JsonObject>();
		for (var i = 0; i < 100; i++) {

			final var payload = new JsonObject();
			payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
			expectedPayloads.add(payload);
			this.assertItemIsNull(this.publish.send(this.testQueue.getInputQueueName(), payload));

		}
		do {

			final var received = TestQueue.waitForPayload(Duration.ofMinutes(1));
			assertNotNull(received);
			assertTrue(expectedPayloads.remove(received));

		} while (!expectedPayloads.isEmpty());

	}

}
