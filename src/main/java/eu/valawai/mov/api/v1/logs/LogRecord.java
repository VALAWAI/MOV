/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.logs;

import java.text.MessageFormat;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import io.vertx.core.json.JsonObject;

/**
 * A log message that has been done in VALAWAI.
 *
 * @author UDT-IA, IIIA-CSIC
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
		 * Set info as the log level.
		 *
		 * @return this component that build the log record.
		 */
		public Builder withInfo() {

			return this.withLevel(LogLevel.INFO);
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
		 * Return the builded log record.
		 *
		 * @return the new log record.
		 */
		public LogRecord build() {

			return this.record;
		}

	}

}