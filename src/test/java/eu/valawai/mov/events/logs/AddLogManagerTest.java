/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.logs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.live.logs.LogEntity;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;

/**
 * Test the {@link AddLogManager}.
 *
 * @see AddLogManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class AddLogManagerTest extends MovEventTestCase {

	/**
	 * The queue name to send the change topology events.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.add_log.queue.name", defaultValue = "valawai/log/add")
	String addLogQueueName;

	/**
	 * Check that cannot add log with an invalid payload.
	 */
	@Test
	public void shouldNotAddLogWithInvalidPayload() {

		final var payload = new AddLogPayload();

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.addLogQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that can add a log message.
	 */
	@Test
	public void shouldAddLog() {

		final var payload = new AddLogPayloadTest().nextModel();

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.addLogQueueName, payload));

		final LogEntity last = this.assertItemNotNull(LogEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= last.timestamp);
		assertEquals(payload.level, last.level);
		assertEquals(payload.message, last.message);
		assertEquals(payload.payload, last.payload);

	}

	/**
	 * Check that can add a log message that contains {.
	 */
	@Test
	public void shouldAddLogWithQuadratorInMesage() {

		final var payload = new AddLogPayloadTest().nextModel();
		payload.message += "{ level: \"ERROR\", message: \"{'patient_id': 'ff6bf1b1-...M...\"} => Invalid log message";
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.addLogQueueName, payload));

		final LogEntity last = this.assertItemNotNull(LogEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= last.timestamp);
		assertEquals(payload.level, last.level);
		assertEquals(payload.message, last.message);
		assertEquals(payload.payload, last.payload);

	}

}
