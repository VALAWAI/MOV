/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.logs;

import java.text.MessageFormat;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import eu.valawai.mov.persistence.logs.AddLogRecord;
import io.quarkus.logging.Log;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * A log message that has been done in VALAWAI.
 *
 * @author VALAWAI
 */
@Schema(title = "A log message that happened on VALAWAI.")
public class LogRecord extends Model {

	/**
	 * The level of the log.
	 */
	@Schema(title = "The level of the log.")
	public LogLevel level;

	/**
	 * The message of the log.
	 */
	@Schema(title = "The log message.")
	public String message;

	/**
	 * The payload of the log.
	 */
	@Schema(title = "The payload associated to the log.")
	public String payload;

	/**
	 * The timestamp when the log has added.
	 */
	@Schema(title = "The timestamp when the .")
	public long timestamp;

	/**
	 * Return the component to build a log record.
	 *
	 * @return the component to create a new log record.
	 */
	public static Builder builder() {

		return new Builder();
	}

	/**
	 * Build a log message.
	 */
	public static class Builder {

		/**
		 * The record that is building.
		 */
		protected LogRecord record = new LogRecord();

		/**
		 * Create the builder.
		 */
		private Builder() {

		}

		/**
		 * Set error as the log level.
		 *
		 * @return this component that build the log record.
		 */
		public Builder withError() {

			return this.withLevel(LogLevel.ERROR);
		}

		/**
		 * Set warning as the log level.
		 *
		 * @return this component that build the log record.
		 */
		public Builder withWarning() {

			return this.withLevel(LogLevel.WARN);
		}

		/**
		 * Set info as the log level.
		 *
		 * @return this component that build the log record.
		 */
		public Builder withInfo() {

			return this.withLevel(LogLevel.INFO);
		}

		/**
		 * Set debug as the log level.
		 *
		 * @return this component that build the log record.
		 */
		public Builder withDebug() {

			return this.withLevel(LogLevel.DEBUG);
		}

		/**
		 * Set level for the log record.
		 *
		 * @param level for the log record.
		 *
		 * @return this component that build the log record.
		 */
		public Builder withLevel(LogLevel level) {

			this.record.level = level;
			return this;
		}

		/**
		 * Set message for the log record.
		 *
		 * @param pattern of the log message.
		 * @param args    to replace on the pattern.
		 *
		 * @return this component that build the log record.
		 */
		public Builder withMessage(String pattern, Object... args) {

			this.record.message = MessageFormat.format(pattern, args);
			return this;
		}

		/**
		 * Set payload for the log record.
		 *
		 * @param payload for the log record.
		 *
		 * @return this component that build the log record.
		 */
		public Builder withPayload(JsonObject payload) {

			this.record.payload = payload.encodePrettily();
			return this;
		}

		/**
		 * Set payload for the log record.
		 *
		 * @param payload for the log record.
		 *
		 * @return this component that build the log record.
		 */
		public Builder withPayload(Object payload) {

			this.record.payload = Json.encodePrettily(payload);
			return this;
		}

		/**
		 * Return the builded log record.
		 *
		 * @return the new log record.
		 */
		public LogRecord build() {

			return this.record;
		}

		/**
		 * Store the log.
		 */
		public void store() {

			final var log = this.build();
			AddLogRecord.fresh().withLog(log).execute().subscribe().with(added -> {

				if (added) {

					Log.debugv("Stored {0}", log);

				} else {

					Log.errorv("Cannot store {0}", log);
				}

				if (log.message != null) {

					switch (log.level) {

					case ERROR:
						Log.error(log.message);
						break;
					case WARN:
						Log.warn(log.message);
						break;
					case INFO:
						Log.info(log.message);
						break;
					default:
						Log.debug(log.message);
						break;
					}

				}

			});

		}

	}

}
