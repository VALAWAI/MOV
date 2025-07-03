/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.AfterEach;

import eu.valawai.mov.MasterOfValawaiTestCase;
import io.smallrye.mutiny.Multi;
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

		return this.assertFailure(this.listener.open(queueName).toUni());

	}

	/**
	 * A queue that can be used to manage the received messages.
	 *
	 * @param messages that has been received.
	 * @param errors   that has been received.
	 */
	public record TestMQQueue(List<JsonObject> messages, List<Throwable> errors) {

		/**
		 * Create a new queue.
		 */
		private TestMQQueue() {

			this(Collections.synchronizedList(new ArrayList<>()), Collections.synchronizedList(new ArrayList<>()));
		}

		/**
		 * Create the queue to listen for the specified queue.
		 *
		 * @param multi to listen for the messages.
		 *
		 * @return the queue to listen for the messages.
		 */
		public static TestMQQueue listenTo(Multi<JsonObject> multi) {

			final var queue = new TestMQQueue();
			multi.subscribe().with(msg -> {

				synchronized (queue) {

					queue.messages.add(msg);
					queue.notifyAll();

				}

			}, error -> {

				synchronized (queue) {

					queue.errors.add(error);
					queue.notifyAll();

				}

			});

			return queue;

		}

		/**
		 * Wait until an element satisfy a filter.
		 *
		 * @param elements to search the element.
		 * @param filter   of the element.
		 * @param duration maximum time to wait.
		 *
		 * @return the element that satisfy the filter.
		 */
		private final <T> T waitElement(List<T> elements, Predicate<T> filter, Duration duration) {

			final var until = System.currentTimeMillis() + duration.toMillis();
			do {

				synchronized (this) {

					final var i = elements.iterator();
					while (i.hasNext()) {

						final var element = i.next();
						if (filter.test(element)) {

							i.remove();
							return element;
						}
					}

					try {

						this.wait(duration.toMillis());

					} catch (final Throwable ignored) {
					}
				}

			} while (System.currentTimeMillis() < until);

			fail("Not received the expected element in the specified time.");
			return null;

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

			return this.waitElement(this.messages, filter, duration);
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

			return this.waitElement(this.errors, filter, duration);
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

		final var queue = TestMQQueue.listenTo(this.listener.open(queueName));
		this.waitUntilQueueIsOpen(queueName, duration);
		return queue;

	}

}
