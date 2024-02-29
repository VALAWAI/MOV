/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.logs;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import eu.valawai.mov.MasterOfValawaiTestCase;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.api.v1.logs.LogRecordTest;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;

/**
 * Test the {@link AddLog}.
 *
 * @see AddLog
 *
 * @author VALAWAI
 */
@QuarkusTest
public class AddLogTest extends MasterOfValawaiTestCase {

	/**
	 * Should add a record.
	 */
	@Test
	public void shouldAddRecord() {

		final var log = new LogRecordTest().nextModel();
		final var result = this.assertExecutionNotNull(AddLog.fresh().withLog(log));
		assertTrue(result);

		final LogEntity stored = this.assertItemNotNull(
				LogEntity.find("level = ?1 and message = ?2", Sort.descending("_id"), log.level.name(), log.message)
						.firstResult());
		assertEquals(log.payload, stored.payload);
		assertEquals(log.timestamp, stored.timestamp);

	}

	/**
	 * Should store a log message.
	 *
	 * @param level of the log message.
	 */
	@ParameterizedTest(name = "Should store a message of the level {0}")
	@EnumSource(LogLevel.class)
	public void shouldStoreLogOf(LogLevel level) {

		final var count = this.assertItemNotNull(LogEntity.count());
		final var message = ValueGenerator.nextPattern("Message of the log {0}");
		AddLog.fresh().withLevel(level).withMessage(message).store();
		this.waitUntil(() -> this.assertItemNotNull(LogEntity.count()), newCount -> newCount != count);

		final Uni<LogEntity> find = LogEntity.findAll(Sort.descending("_id")).firstResult();
		final var last = this.assertItemNotNull(find);
		assertEquals(level, last.level);
		assertEquals(message, last.message);

	}

	/**
	 * Should store an error log message.
	 */
	@Test
	public void shouldStoreErrorLog() {

		final var count = this.assertItemNotNull(LogEntity.count());
		final var message = ValueGenerator.nextPattern("Message of the log {0}");
		final var payload = new HashMap<String, Object>();
		payload.put("int", rnd().nextInt());
		payload.put("flip", flipCoin());
		AddLog.fresh().withError().withMessage(message).withPayload(payload).store();
		this.waitUntil(() -> this.assertItemNotNull(LogEntity.count()), newCount -> newCount != count);

		final Uni<LogEntity> find = LogEntity.findAll(Sort.descending("_id")).firstResult();
		final var last = this.assertItemNotNull(find);
		assertEquals(LogLevel.ERROR, last.level);
		assertEquals(message, last.message);
		final var json = new JsonObject(payload);
		assertEquals(json.encodePrettily(), last.payload);

	}

	/**
	 * Should store an warn log message.
	 */
	@Test
	public void shouldStoreWarnLog() {

		final var count = this.assertItemNotNull(LogEntity.count());
		final var message = ValueGenerator.nextPattern("Message of the log {0}");
		final var payload = new HashMap<String, Object>();
		payload.put("int", rnd().nextInt());
		payload.put("flip", flipCoin());
		final var json = new JsonObject(payload);
		AddLog.fresh().withWarning().withMessage(message).withPayload(json).store();
		this.waitUntil(() -> this.assertItemNotNull(LogEntity.count()), newCount -> newCount != count);
		final Uni<LogEntity> find = LogEntity.findAll(Sort.descending("_id")).firstResult();
		final var last = this.assertItemNotNull(find);
		assertEquals(LogLevel.WARN, last.level);
		assertEquals(message, last.message);
		assertEquals(json.encodePrettily(), last.payload);

	}

	/**
	 * Should store an info log message.
	 */
	@Test
	public void shouldStoreInfoLog() {

		final var count = this.assertItemNotNull(LogEntity.count());
		final var message = ValueGenerator.nextPattern("Message of the log {0}");
		final var payload = new HashMap<String, Object>();
		payload.put("int", rnd().nextInt());
		payload.put("flip", flipCoin());
		AddLog.fresh().withInfo().withMessage(message).withPayload(payload).store();
		this.waitUntil(() -> this.assertItemNotNull(LogEntity.count()), newCount -> newCount != count);

		final Uni<LogEntity> find = LogEntity.findAll(Sort.descending("_id")).firstResult();
		final var last = this.assertItemNotNull(find);
		assertEquals(LogLevel.INFO, last.level);
		assertEquals(message, last.message);
		final var json = new JsonObject(payload);
		assertEquals(json.encodePrettily(), last.payload);

	}

	/**
	 * Should store an debug log message.
	 */
	@Test
	public void shouldStoreDebugLog() {

		final var count = this.assertItemNotNull(LogEntity.count());
		final var message = ValueGenerator.nextPattern("Message of the log {0}");
		final var payload = new HashMap<String, Object>();
		payload.put("int", rnd().nextInt());
		payload.put("flip", flipCoin());
		final var json = new JsonObject(payload);
		AddLog.fresh().withDebug().withMessage(message).withPayload(json).store();
		this.waitUntil(() -> this.assertItemNotNull(LogEntity.count()), newCount -> newCount != count);
		final Uni<LogEntity> find = LogEntity.findAll(Sort.descending("_id")).firstResult();
		final var last = this.assertItemNotNull(find);
		assertEquals(LogLevel.DEBUG, last.level);
		assertEquals(message, last.message);
		assertEquals(json.encodePrettily(), last.payload);

	}
}
