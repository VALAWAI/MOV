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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;

import eu.valawai.mov.MasterOfValawaiTestCase;
import io.quarkus.logging.Log;
import io.quarkus.test.common.QuarkusTestResource;
import jakarta.inject.Inject;

/**
 * Generic test over the components that manage an event.
 *
 * @author VALAWAI
 */
@QuarkusTestResource(RabbitMQTestResource.class)
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

}
