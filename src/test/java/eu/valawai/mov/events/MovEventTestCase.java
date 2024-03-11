/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.junit.jupiter.api.AfterEach;

import eu.valawai.mov.MasterOfValawaiTestCase;
import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;

/**
 * Generic test over the components that manage an event.
 *
 * @author VALAWAI
 */
public class MovEventTestCase extends MasterOfValawaiTestCase {

	/**
	 * The service to send events.
	 */
	@Inject
	protected PublishService publish;

	/**
	 * The service to listen for events.
	 */
	@Inject
	protected ListenerService listener;

	/**
	 * The names of the queues that has been opened.
	 */
	protected Set<String> openedQueues = new HashSet<>();

	/**
	 * Check that publish the specified payload.
	 *
	 * @param queueName name of the queue to publish a message.
	 * @param payload   of the message to publish.
	 */
	protected void assertPublish(String queueName, Object payload) {

		this.assertItemIsNull(this.publish.send(queueName, payload));

	}

	/**
	 * Close the opened queues.
	 */
	@AfterEach
	public void shouldCloseOpenedQueues() {

		for (final var queueName : this.openedQueues) {

			if (this.listener.isOpen(queueName)) {

				this.assertItemIsNull(this.listener.close(queueName));
			}
		}

		this.openedQueues.clear();
	}

	/**
	 * Open a queue and wait it is opened and a maximum of 30 seconds.
	 *
	 * @param queueName name of the queue to wait too be open.
	 */
	public void waitUntilQueueIsOpen(String queueName) {

		this.waitUntilQueueIsOpen(queueName, Duration.ofSeconds(30));

	}

	/**
	 * Open a queue and wait it is opened.
	 *
	 * @param queueName name of the queue to wait too be open.
	 * @param duration  maximum time to wait.
	 */
	public void waitUntilQueueIsOpen(String queueName, Duration duration) {

		final var deadline = System.currentTimeMillis() + duration.toMillis();
		while (!this.listener.isOpen(queueName)) {

			if (System.currentTimeMillis() > deadline) {

				fail();

			} else {

				try {
					Thread.sleep(100);
				} catch (final InterruptedException ignored) {
				}

			}

		}
		this.openedQueues.add(queueName);

	}

	/**
	 * Check that can not open a queue.
	 *
	 * @param queueName name of the queue that can not be opened.
	 *
	 * @return the cause that the queue can not be opened.
	 */
	public Throwable assertNotOpenQueue(String queueName) {

		final List<Throwable> errors = new ArrayList<>();
		final var semaphore = new Semaphore(0);
		this.listener.open(queueName).subscribe().with(msg -> {

			Log.error("Unexpected message");
			semaphore.release();

		}, error -> {

			errors.add(error);
			semaphore.release();
		});

		try {

			semaphore.tryAcquire(1, TimeUnit.MINUTES);

		} catch (final InterruptedException ignored) {
		}

		assertEquals(1, errors.size());
		return errors.get(0);

	}

	/**
	 * A queue that can be used to manage the received messages.
	 *
	 * @param messages  that has been received.
	 * @param errors    that has been received.
	 * @param semaphore to release for every error or received message.
	 */
	public record TestMQQueue(List<JsonObject> messages, List<Throwable> errors, Semaphore semaphore) {

		/**
		 * Create a new queue.
		 */
		public TestMQQueue() {

			this(Collections.synchronizedList(new ArrayList<>()), Collections.synchronizedList(new ArrayList<>()),
					new Semaphore(0));
		}

		/**
		 * Wait until received a message that satisfy a filter.
		 *
		 * @param filter   of the waiting message.
		 * @param duration maximum time to wait.
		 *
		 * @return the received message.
		 *
		 * @throws AssertionError if no message received.
		 */
		public JsonObject waitReceiveMessage(Predicate<JsonObject> filter, Duration duration) {

			synchronized (this.messages) {

				final var max = this.messages.size();
				for (var i = 0; i < max; i++) {

					final var msg = this.messages.get(i);
					if (filter.test(msg)) {

						this.messages.remove(i);
						return msg;
					}
				}

				try {

					this.semaphore.tryAcquire(duration.toMillis(), TimeUnit.MILLISECONDS);

				} catch (final Throwable ignored) {
				}
			}

			synchronized (this.messages) {

				final var max = this.messages.size();
				for (var i = 0; i < max; i++) {

					final var msg = this.messages.get(i);
					if (filter.test(msg)) {

						this.messages.remove(i);
						return msg;
					}
				}
			}

			fail("Not received the message in the specified time.");
			return null;
		}

		/**
		 * Wait at maximum 30 seconds to receive any message.
		 *
		 * @return the received message.
		 *
		 * @throws AssertionError if no message received.
		 */
		public JsonObject waitReceiveMessage() {

			return this.waitReceiveMessage(any -> true, Duration.ofSeconds(30));
		}

		/**
		 * Wait at maximum 30 seconds to receive any message with the specified payload.
		 *
		 * @param payloadType to receive
		 *
		 * @return the received payload.
		 *
		 * @throws AssertionError if no message received.
		 */
		public <T> T waitReceiveMessage(Class<T> payloadType) {

			final var msg = this.waitReceiveMessage(payload -> {

				try {

					payload.mapTo(payloadType);
					return true;

				} catch (final Throwable isNotTheExpectedMessage) {

					return false;
				}

			}, Duration.ofSeconds(30));

			return msg.mapTo(payloadType);
		}

		/**
		 * Wait until received a error that satisfy a filter.
		 *
		 * @param filter   of the waiting error.
		 * @param duration maximum time to wait.
		 *
		 * @return the received error.
		 *
		 * @throws AssertionError if no error received.
		 */
		public Throwable waitReceiveError(Predicate<Throwable> filter, Duration duration) {

			synchronized (this.errors) {

				final var max = this.errors.size();
				for (var i = 0; i < max; i++) {

					final var error = this.errors.get(i);
					if (filter.test(error)) {

						this.errors.remove(i);
						return error;
					}
				}

				try {

					this.semaphore.tryAcquire(duration.toMillis(), TimeUnit.MILLISECONDS);

				} catch (final Throwable ignored) {
				}
			}

			synchronized (this.errors) {

				final var max = this.errors.size();
				for (var i = 0; i < max; i++) {

					final var error = this.errors.get(i);
					if (filter.test(error)) {

						this.errors.remove(i);
						return error;
					}
				}
			}

			fail("Not received the error in the specified time.");
			return null;
		}

		/**
		 * Wait at maximum 30 seconds to receive any error.
		 *
		 * @return the received error.
		 *
		 * @throws AssertionError if no error received.
		 */
		public Throwable waitReceiveError() {

			return this.waitReceiveError(any -> true, Duration.ofSeconds(30));
		}
	}

	/**
	 * Open a queue and wait at maximum 30 seconds to be opened.
	 *
	 * @param queueName name of the queue to open.
	 *
	 * @return the opened queue.
	 */
	public TestMQQueue waitOpenQueue(String queueName) {

		return this.waitOpenQueue(queueName, Duration.ofSeconds(30));

	}

	/**
	 * Open a queue and wait at maximum the duration to be opened.
	 *
	 * @param queueName name of the queue to open.
	 * @param duration  maximum time to wait.
	 *
	 * @return the opened queue.
	 */
	public TestMQQueue waitOpenQueue(String queueName, Duration duration) {

		final var queue = new TestMQQueue();
		this.listener.open(queueName).subscribe().with(msg -> {

			queue.messages.add(msg);
			queue.semaphore.release();

		}, error -> {

			queue.errors.add(error);
			queue.semaphore.release();
		});
		this.waitUntilQueueIsOpen(queueName, duration);
		return queue;

	}

}
