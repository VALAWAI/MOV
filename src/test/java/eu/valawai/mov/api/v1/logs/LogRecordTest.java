/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.logs;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextPastTime;
import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.persistence.logs.LogEntity;
import io.vertx.core.json.JsonObject;

/**
 * Test the {@link LogRecord}.
 *
 * @see LogRecord
 *
 * @author VALAWAI
 */
public class LogRecordTest extends ModelTestCase<LogRecord> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LogRecord createEmptyModel() {

		return new LogRecord();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(LogRecord model) {

		model.level = next(LogLevel.values());
		model.message = nextPattern("Message of the log {0}");
		if (flipCoin()) {

			final var values = new HashMap<String, Object>();
			values.put("number", rnd().nextInt());
			values.put("flip", flipCoin());
			values.put("message", nextPattern("Message {0}"));
			model.payload = new JsonObject(values).encodePrettily();

		}
		model.timestamp = nextPastTime();

	}

	/**
	 * Assert build with error level.
	 */
	@Test
	public void shouldBuildWithErrorLevel() {

		final var log = LogRecord.builder().withError().build();
		assertEquals(LogLevel.ERROR, log.level);

	}

	/**
	 * Assert build with info level.
	 */
	@Test
	public void shouldBuildWithInfoLevel() {

		final var log = LogRecord.builder().withInfo().build();
		assertEquals(LogLevel.INFO, log.level);

	}

	/**
	 * Assert build with message.
	 */
	@Test
	public void shouldBuildWithMessage() {

		final var value = rnd().nextInt(0, 999);
		final var message = "Message of " + value;
		final var log = LogRecord.builder().withMessage("Message of {0}", value).build();
		assertEquals(message, log.message);

	}

	/**
	 * Assert build with payload.
	 */
	@Test
	public void shouldBuildWithPayload() {

		final var values = new HashMap<String, Object>();
		values.put("int", rnd().nextInt());
		values.put("flip", flipCoin());
		final var payload = new JsonObject(values);
		final var log = LogRecord.builder().withPayload(payload).build();
		assertEquals(payload.encodePrettily(), log.payload);

	}

	/**
	 * Return the model from an entity.
	 *
	 * @param entity to get the model.
	 *
	 * @return the model from the entity.
	 */
	public static LogRecord from(LogEntity entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new LogRecord();
			model.level = entity.level;
			model.message = entity.message;
			model.payload = entity.payload;
			model.timestamp = entity.timestamp;

			return model;

		}
	}

}
