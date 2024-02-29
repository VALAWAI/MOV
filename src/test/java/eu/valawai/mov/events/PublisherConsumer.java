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

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * The test component to capture messages from a publisher.
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class PublisherConsumer {

	/**
	 * The component to store the received messages.
	 */
	private static List<JsonObject> QUEUE = Collections.synchronizedList(new ArrayList<>());

	/**
	 * Called when a message is send to the queue.
	 *
	 * @param payload of the received messages.
	 */
	@Incoming("publisher_service_test")
	public void consume(JsonObject payload) {

		synchronized (QUEUE) {

			QUEUE.add(payload);
			QUEUE.notifyAll();

		}
	}

	/**
	 * Remove all the messages of the queue.
	 */
	public static void clear() {

		QUEUE.clear();

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

			}

			if (QUEUE.isEmpty()) {

				return null;

			} else {

				return QUEUE.remove(0);
			}
		}

	}

}
