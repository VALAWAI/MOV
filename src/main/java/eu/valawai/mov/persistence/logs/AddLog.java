/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.logs;

import java.text.MessageFormat;

import org.bson.types.ObjectId;

import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.api.v1.logs.LogRecord;
import eu.valawai.mov.persistence.AbstractEntityOperator;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * Add a new log record into the database.
 *
 * @see LogEntity
 * @see LogRecord
 *
 * @author VALAWAI
 */
public class AddLog extends AbstractEntityOperator<Boolean, AddLog> {

	/**
	 * The record that is building.
	 */
	protected LogEntity log;

	/**
	 * The error for the log.
	 */
	protected Throwable error;

	/**
	 * Create the operator.
	 */
	private AddLog() {

		this.log = new LogEntity();
	}

	/**
	 * Create a new add log record operation.
	 *
	 * @return the operation to store a log record.
	 */
	public static AddLog fresh() {

		return new AddLog();

	}

	/**
	 * Set error as the log level and an error exception.
	 *
	 * @param error that has generated this log message.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withError(Throwable error) {

		return this.withException(error).withError();
	}

	/**
	 * Set error as the log level.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withError() {

		return this.withLevel(LogLevel.ERROR);
	}

	/**
	 * Set warning as the log level and an error exception.
	 *
	 * @param error that has generated this log message.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withWarning(Throwable error) {

		return this.withException(error).withWarning();
	}

	/**
	 * Set the exception associated to the log message.
	 *
	 * @param error that has generated this log message.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withException(Throwable error) {

		this.error = error;
		if (this.log.message == null) {

			return this.withMessage(error.getMessage());

		} else {

			return this;
		}
	}

	/**
	 * Set warning as the log level.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withWarning() {

		return this.withLevel(LogLevel.WARN);
	}

	/**
	 * Set info as the log level.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withInfo() {

		return this.withLevel(LogLevel.INFO);
	}

	/**
	 * Set debug as the log level.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withDebug() {

		return this.withLevel(LogLevel.DEBUG);
	}

	/**
	 * Set level for the log.
	 *
	 * @param level for the log.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withLevel(LogLevel level) {

		this.log.level = level;
		return this;
	}

	/**
	 * Set message for the log.
	 *
	 * @param pattern of the log message.
	 * @param args    to replace on the pattern.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withMessage(String pattern, Object... args) {

		this.log.message = MessageFormat.format(pattern, args);
		return this;
	}

	/**
	 * Set payload for the log.
	 *
	 * @param payload for the log.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withPayload(JsonObject payload) {

		String str = null;
		if (payload != null) {

			str = payload.encodePrettily();
		}
		return this.withPayload(str);
	}

	/**
	 * Set payload for the log.
	 *
	 * @param payload for the log.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withPayload(Object payload) {

		String str = null;
		if (payload != null) {

			str = Json.encodePrettily(payload);
		}
		return this.withPayload(str);
	}

	/**
	 * Set payload for the log.
	 *
	 * @param payload for the log.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withPayload(String payload) {

		this.log.payload = payload;
		return this;
	}

	/**
	 * Specify the component associated to the log message.
	 *
	 * @param componentId identifier of the component that has generated the
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withComponent(ObjectId componentId) {

		this.log.componentId = componentId;
		return this;
	}

	/**
	 * Set record for the log.
	 *
	 * @param record to be stored as log.
	 *
	 * @return the operation to store a log record.
	 */
	public AddLog withLog(LogRecord record) {

		this.log.level = record.level;
		this.log.message = record.message;
		this.log.timestamp = record.timestamp;
		this.log.payload = record.payload;
		if (record.component != null) {

			this.log.componentId = record.component.id;
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uni<Boolean> execute() {

		return this.log.persist().onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot store the {0}", this.log);
			return null;

		}).map(result -> result != null);
	}

	/**
	 * Store the log.
	 *
	 * @see #execute()
	 */
	public void store() {

		this.execute().subscribe().with(added -> {

			if (this.log.message != null) {

				switch (this.log.level) {

				case ERROR:
					Log.errorv(this.error, this.log.message);
					break;
				case WARN:
					Log.warnv(this.error, this.log.message);
					break;
				case INFO:
					Log.infov(this.error, this.log.message);
					break;
				default:
					Log.debugv(this.error, this.log.message);
					break;
				}

			} else if (added) {

				Log.debugv("Stored {0}", this.log);

			} else {

				Log.errorv("Cannot store {0}", this.log);
			}

		});

	}

}
