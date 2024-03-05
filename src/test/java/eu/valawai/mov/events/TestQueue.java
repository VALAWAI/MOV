/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The test component to capture messages from a publisher.
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class TestQueue {

	/**
	 * The component to store the received messages.
	 */
	private static List<JsonObject> QUEUE = Collections.synchronizedList(new ArrayList<>());

	/**
	 * The default name of the channel to send events.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.test_in.queue.name", defaultValue = "test/queue/in")
	String inputQueueName;

	/**
	 * The default name of the channel to send events.
	 */
	@ConfigProperty(name = "mp.messaging.outgoing.test_out.queue.name", defaultValue = "test/queue/out")
	String outputQueueName;

	/**
	 * The component to send messages to the queue.
	 */
	@Inject
	@Channel("test_out")
	Emitter<Object> emitter;

	/**
	 * Called when a message is send to the queue.
	 *
	 * @param payload of the received messages.
	 */
	@Incoming("test_in")
	public void consume(JsonObject payload) {

		synchronized (QUEUE) {

			QUEUE.add(payload);
			QUEUE.notifyAll();

		}
	}

	/**
	 * Send a message to the test queue.
	 *
	 * @param payload to send.
	 */
	public void send(JsonObject payload) {

		this.emitter.send(payload).whenComplete((any, error) -> {

			if (error != null) {

				error.printStackTrace();
			}

		});
	}

	/**
	 * The input queue name.
	 *
	 * @return the name of the input queue name.
	 */
	public String getInputQueueName() {

		return this.inputQueueName;
	}

	/**
	 * The output queue name.
	 *
	 * @return the name of the output queue name.
	 */
	public String getOutputQueueName() {

		return this.outputQueueName;
	}

	/**
	 * Remove all the messages of the queue.
	 */
	public static void clear() {

		QUEUE.clear();

	}

	/**
	 * Return the next payload received message waiting at most for 30 seconds.
	 *
	 * @return the received payload or {@code null} if any message is received.
	 */
	public static JsonObject waitForPayload() {

		return waitForPayload(Duration.ofSeconds(30));
	}

	/**
	 * Return the next payload received message.
	 *
	 * @param duration that it must wait.
	 *
	 * @return the received payload or {@code null} if any message is received.
	 */
	public static JsonObject waitForPayload(Duration duration) {

		synchronized (QUEUE) {

			if (QUEUE.isEmpty()) {

				try {

					QUEUE.wait(duration.toMillis());

				} catch (final InterruptedException ignored) {
				}

				if (QUEUE.isEmpty()) {

					return null;

				}
			}
			return QUEUE.remove(0);

		}

	}

}
