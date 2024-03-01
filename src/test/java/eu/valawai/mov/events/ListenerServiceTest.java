/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
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
	 * Check not close with {@code null} queue name.
	 */
	@Test
	public void shouldNotCloseWithNullQueueName() {

		final var error = this.assertFailure(this.service.close(null));
		assertInstanceOf(IllegalArgumentException.class, error);

	}

	/**
	 * Check not close undefined queue name.
	 */
	@Test
	public void shouldNotCloseUndefinedQueueName() {

		final var queueName = ValueGenerator.nextPattern("queue_name_{0}");
		final var error = this.assertFailure(this.service.close(queueName));
		assertInstanceOf(IllegalArgumentException.class, error);

	}

	/**
	 * Check that can not register two times to the same queue.
	 *
	 * @throws InterruptedException If the thread is interrupted.
	 */
	@Test
	public void shoulnotOpenTwoTimesTheSameChanel() throws InterruptedException {

		final var queueName = ValueGenerator.nextPattern("queue_name_{0}");
		final List<Throwable> errors = new ArrayList<>();
		this.service.open(queueName).subscribe().with(msg -> Log.error("Unexpected message"));
		final var semaphore = new Semaphore(0);
		this.service.open(queueName).subscribe().with(msg -> Log.error("Unexpected message"), error -> {
			errors.add(error);
			semaphore.release();
		});
		try {

			semaphore.tryAcquire(1, TimeUnit.MINUTES);

			assertEquals(1, errors.size());
			assertInstanceOf(IllegalArgumentException.class, errors.get(0));

		} finally {

			this.assertItemIsNull(this.service.close(queueName));
		}
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
		final List<String> queueNames = new ArrayList<>();
		try {

			for (var i = 0; i < 10; i++) {

				final var queueName = ValueGenerator.nextPattern("queue_name_{0}");
				queueNames.add(queueName);
				this.service.open(queueName).subscribe().with(payload -> {

					receivedPayloads.add(payload);
					semaphore.release();

				}, error -> {

					semaphore.release();
				});
			}

			final var expectedPayloads = new ArrayList<JsonObject>();
			for (final var queueName : queueNames) {

				final var payload = new JsonObject();
				payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
				expectedPayloads.add(payload);
				this.assertItemIsNull(this.publisher.send(queueName, payload));

			}

			semaphore.tryAcquire(expectedPayloads.size(), 1, TimeUnit.MINUTES);

			assertEquals(expectedPayloads.size(), receivedPayloads.size());
			for (final var payload : expectedPayloads) {

				assertTrue(receivedPayloads.remove(payload));

			}

		} finally {

			for (final var queueName : queueNames) {

				this.service.close(queueName);
			}
		}

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
		final var queueName = ValueGenerator.nextPattern("queue_name_{0}");
		this.service.open(queueName).subscribe().with(payload -> {

			receivedPayloads.add(payload);
			semaphore.release();

		}, error -> {

			semaphore.release();
		});
		try {

			final var expectedPayloads = new ArrayList<JsonObject>();
			for (var i = 0; i < 100; i++) {

				final var payload = new JsonObject();
				payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
				expectedPayloads.add(payload);
				this.assertItemIsNull(this.publisher.send(queueName, payload));

			}

			semaphore.tryAcquire(expectedPayloads.size(), 1, TimeUnit.MINUTES);

			assertEquals(expectedPayloads.size(), receivedPayloads.size());
			for (final var payload : expectedPayloads) {

				assertTrue(receivedPayloads.remove(payload));

			}

		} finally {

			this.assertItemIsNull(this.service.close(queueName));
		}

	}

	/**
	 * Check listen messages from emitter.
	 *
	 * @throws InterruptedException If the thread is interrupted.
	 */
	@Test
	public void shouldReceiveAlotFromEmitter() throws InterruptedException {

		final var semaphore = new Semaphore(0);
		final var receivedPayloads = Collections.synchronizedList(new ArrayList<JsonObject>());
		this.service.open(this.channelName).subscribe().with(payload -> {

			receivedPayloads.add(payload);
			semaphore.release();

		}, error -> {

			semaphore.release();
		});
		try {

			final var expectedPayloads = new ArrayList<JsonObject>();
			for (var i = 0; i < 100; i++) {

				final var payload = new JsonObject();
				payload.put("pattern", ValueGenerator.nextPattern("pattern_{0}"));
				expectedPayloads.add(payload);
				this.emitter.send(payload);
			}

			semaphore.tryAcquire(expectedPayloads.size(), 1, TimeUnit.MINUTES);

			assertEquals(expectedPayloads.size(), receivedPayloads.size());
			for (final var payload : expectedPayloads) {

				assertTrue(receivedPayloads.remove(payload));

			}

		} finally {

			this.assertItemIsNull(this.service.close(this.channelName));
		}
	}
}
