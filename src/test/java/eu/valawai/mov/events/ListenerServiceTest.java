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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.ValueGenerator;
import io.quarkus.logging.Log;
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
	 * A component that receive messages form a queue used for testing.
	 */
	@Inject
	TestQueue testQueue;

	/**
	 * Check not create open with {@code null} queue name.
	 */
	@Test
	public void shouldNotOpenWithNullinputQueueName() {

		final var error = this.assertNotOpenQueue(null);
		assertInstanceOf(IllegalArgumentException.class, error);

	}

	/**
	 * Check taht ois not open a {@code null} queue name.
	 */
	@Test
	public void shouldNotBeOpenedANullinputQueueName() {

		assertFalse(this.listener.isOpen(null));

	}

	/**
	 * Check not close with {@code null} queue name.
	 */
	@Test
	public void shouldNotCloseWithNullinputQueueName() {

		final var error = this.assertFailure(this.listener.close(null));
		assertInstanceOf(IllegalArgumentException.class, error);

	}

	/**
	 * Check not close undefined queue name.
	 */
	@Test
	public void shouldNotCloseUndefinedinputQueueName() {

		final var inputQueueName = ValueGenerator.nextPattern("queue_name_{0}");
		final var error = this.assertFailure(this.listener.close(inputQueueName));
		assertInstanceOf(IllegalArgumentException.class, error);

	}

	/**
	 * Check that can not register two times to the same queue.
	 *
	 * @throws InterruptedException If the thread is interrupted.
	 */
	@Test
	public void shoulNotOpenTwoTimesTheSameChanel() throws InterruptedException {

		final var inputQueueName = ValueGenerator.nextPattern("queue_name_{0}");
		this.listener.open(inputQueueName).subscribe().with(msg -> Log.error("Unexpected message"));
		var error = this.assertNotOpenQueue(inputQueueName);
		assertInstanceOf(IllegalArgumentException.class, error);

		this.waitUntilQueueIsOpen(inputQueueName);
		error = this.assertNotOpenQueue(inputQueueName);
		assertInstanceOf(IllegalArgumentException.class, error);

	}

	/**
	 * Check listen messages from publisher.
	 *
	 * @throws InterruptedException If the thread is interrupted.
	 */
	@Test
	public void shouldOpenMultipleListeners() throws InterruptedException {

		final var semaphore = new Semaphore(0);
		final var receivedPayloads = Collections.synchronizedList(new ArrayList<JsonObject>());
		final List<String> inputQueueNames = new ArrayList<>();
		for (var i = 0; i < 10; i++) {

			final var inputQueueName = ValueGenerator.nextPattern("queue_name_{0}");
			inputQueueNames.add(inputQueueName);
			this.listener.open(inputQueueName).subscribe().with(payload -> {

				if (inputQueueName.equals(payload.getValue("inputQueueName"))) {

					receivedPayloads.add(payload);
					semaphore.release();
				}

			}, error -> {

				error.printStackTrace();
				semaphore.release();
			});
			this.waitUntilQueueIsOpen(inputQueueName);
		}

		final var expectedPayloads = new ArrayList<JsonObject>();
		for (final var inputQueueName : inputQueueNames) {

			final var payload = new JsonObject();
			payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
			payload.put("inputQueueName", inputQueueName);
			expectedPayloads.add(payload);
			this.assertPublish(inputQueueName, payload);

		}

		semaphore.tryAcquire(expectedPayloads.size(), 30, TimeUnit.SECONDS);

		assertEquals(expectedPayloads.size(), receivedPayloads.size());
		for (final var payload : expectedPayloads) {

			assertTrue(receivedPayloads.remove(payload));

		}

	}

	/**
	 * Check send a messages to a listener.
	 *
	 * @throws InterruptedException If the thread is interrupted.
	 */
	@Test
	public void shouldSendAMessageToAListener() throws InterruptedException {

		final var semaphore = new Semaphore(0);
		final var receivedPayloads = Collections.synchronizedList(new ArrayList<JsonObject>());
		final var inputQueueName = ValueGenerator.nextPattern("queue_name_{0}");
		this.listener.open(inputQueueName).subscribe().with(payload -> {

			if (inputQueueName.equals(payload.getValue("inputQueueName"))) {

				receivedPayloads.add(payload);
				semaphore.release();
			}

		}, error -> {

			error.printStackTrace();
			semaphore.release();
		});
		this.waitUntilQueueIsOpen(inputQueueName);

		final var payload = new JsonObject();
		payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
		payload.put("inputQueueName", inputQueueName);
		this.assertPublish(inputQueueName, payload);

		semaphore.tryAcquire(1, 30, TimeUnit.SECONDS);

		assertEquals(1, receivedPayloads.size());
		assertEquals(payload, receivedPayloads.get(0));

	}

	/**
	 * Check send multiple messages to a listener.
	 *
	 * @throws InterruptedException If the thread is interrupted.
	 */
	@Test
	public void shouldSendMultipleMessagesToTheSameListener() throws InterruptedException {

		final var semaphore = new Semaphore(0);
		final var receivedPayloads = Collections.synchronizedList(new ArrayList<JsonObject>());
		final var inputQueueName = ValueGenerator.nextPattern("queue_name_{0}");
		this.listener.open(inputQueueName).subscribe().with(payload -> {

			if (inputQueueName.equals(payload.getValue("inputQueueName"))) {

				receivedPayloads.add(payload);
				semaphore.release();
			}

		}, error -> {

			error.printStackTrace();
			semaphore.release();
		});
		this.waitUntilQueueIsOpen(inputQueueName);

		final var expectedPayloads = new ArrayList<JsonObject>();
		for (var i = 0; i < 100; i++) {

			final var payload = new JsonObject();
			payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
			payload.put("inputQueueName", inputQueueName);
			expectedPayloads.add(payload);
			this.assertPublish(inputQueueName, payload);

		}

		semaphore.tryAcquire(expectedPayloads.size(), 30, TimeUnit.SECONDS);

		assertEquals(expectedPayloads.size(), receivedPayloads.size());
		for (final var payload : expectedPayloads) {

			assertTrue(receivedPayloads.remove(payload));

		}

	}

	/**
	 * Check send multiple messages to multiple listeners.
	 *
	 * @throws InterruptedException If the thread is interrupted.
	 */
	@Test
	public void shouldSendMultipleMessagesToMultipleListeners() throws InterruptedException {

		final var semaphore = new Semaphore(0);
		final Map<String, List<JsonObject>> receivedMessagesByQueue = Collections.synchronizedMap(new HashMap<>());
		for (var i = 0; i < 100; i++) {

			final var inputQueueName = ValueGenerator.nextPattern("queue_name_{0}");
			receivedMessagesByQueue.put(inputQueueName, new ArrayList<>());
			this.listener.open(inputQueueName).subscribe().with(payload -> {

				if (inputQueueName.equals(payload.getValue("inputQueueName"))) {

					receivedMessagesByQueue.get(inputQueueName).add(payload);
					semaphore.release();
				}

			}, error -> {

				error.printStackTrace();
				semaphore.release();
			});
			this.waitUntilQueueIsOpen(inputQueueName);
		}

		final var expectedPayloads = new ArrayList<JsonObject>();
		final var inputQueueNames = new ArrayList<>(receivedMessagesByQueue.keySet());
		final var max = inputQueueNames.size() * inputQueueNames.size();
		for (var i = 0; i < max; i++) {

			final var payload = new JsonObject();
			payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
			final var inputQueueName = ValueGenerator.next(inputQueueNames);
			payload.put("inputQueueName", inputQueueName);
			expectedPayloads.add(payload);
			this.assertPublish(inputQueueName, payload);

		}

		semaphore.tryAcquire(max, 1, TimeUnit.MINUTES);
		for (final var payload : expectedPayloads) {

			final var inputQueueName = payload.getString("inputQueueName");
			final var received = receivedMessagesByQueue.get(inputQueueName);
			assertNotNull(received, "Not received the message " + payload.encodePrettily());
			assertTrue(received.remove(payload), "Not received the message " + payload.encodePrettily());
			if (received.isEmpty()) {

				receivedMessagesByQueue.remove(inputQueueName);
			}
		}

		assertTrue(receivedMessagesByQueue.isEmpty(), "Received some unexpected messages");

	}

	/**
	 * Check receive a message from an emitter.
	 *
	 * @throws InterruptedException If the thread is interrupted.
	 */
	@Test
	public void shouldReceiveAMessageFromAnEmitter() throws InterruptedException {

		final var semaphore = new Semaphore(0);
		final var receivedPayloads = Collections.synchronizedList(new ArrayList<JsonObject>());
		final var queueName = this.testQueue.getOutputQueueName();
		this.listener.open(queueName).subscribe().with(payload -> {

			if (this.testQueue.getOutputQueueName().equals(payload.getValue("queueName"))) {

				receivedPayloads.add(payload);
				semaphore.release();
			}

		}, error -> {

			error.printStackTrace();
			semaphore.release();
		});
		this.waitUntilQueueIsOpen(queueName);

		final var payload = new JsonObject();
		payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
		payload.put("queueName", queueName);
		this.testQueue.send(payload);

		semaphore.tryAcquire(30, TimeUnit.SECONDS);

		assertEquals(1, receivedPayloads.size());
		assertEquals(payload, receivedPayloads.get(0));

	}

	/**
	 * Check receive multiple messages from an emitter.
	 *
	 * @throws InterruptedException If the thread is interrupted.
	 */
	@Test
	public void shouldReceiveSomeMessagesFromEmitter() throws InterruptedException {

		final var semaphore = new Semaphore(0);
		final var receivedPayloads = Collections.synchronizedList(new ArrayList<JsonObject>());
		final var queueName = this.testQueue.getOutputQueueName();
		this.listener.open(queueName).subscribe().with(payload -> {

			if (queueName.equals(payload.getValue("queueName"))) {

				receivedPayloads.add(payload);
				semaphore.release();
			}

		}, error -> {

			error.printStackTrace();
			semaphore.release();
		});
		this.waitUntilQueueIsOpen(queueName);

		final var expectedPayloads = new ArrayList<JsonObject>();
		for (var i = 0; i < 100; i++) {

			final var payload = new JsonObject();
			payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
			payload.put("queueName", queueName);
			expectedPayloads.add(payload);
			this.testQueue.send(payload);
		}

		semaphore.tryAcquire(expectedPayloads.size(), 30, TimeUnit.SECONDS);

		assertEquals(expectedPayloads.size(), receivedPayloads.size());
		for (final var payload : expectedPayloads) {

			assertTrue(receivedPayloads.remove(payload));

		}
	}
}
