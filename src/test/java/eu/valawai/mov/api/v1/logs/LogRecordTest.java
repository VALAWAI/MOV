/*
  Copyright 2022-2026 VALAWAI

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

import java.util.HashMap;

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
