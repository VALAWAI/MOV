/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.logs;

import eu.valawai.mov.api.v1.logs.LogRecord;
import eu.valawai.mov.persistence.AbstractEntityOperator;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Add a new log record into the database.
 *
 * @see LogEntity
 * @see LogRecord
 *
 * @author VALAWAI
 */
public class AddLogRecord extends AbstractEntityOperator<Boolean, AddLogRecord> {

	/**
	 * The log record to add.
	 */
	protected LogRecord log;

	/**
	 * Create the operator.
	 */
	private AddLogRecord() {
	}

	/**
	 * Create a new add log record operation.
	 *
	 * @return the operation to store a log record.
	 */
	public static AddLogRecord fresh() {

		return new AddLogRecord();

	}

	/**
	 * Set the log record to store.
	 *
	 * @param log to store.
	 *
	 * @return this operation.
	 */
	public AddLogRecord withLog(LogRecord log) {

		this.log = log;
		return this.operator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uni<Boolean> execute() {

		final LogEntity entity = new LogEntity();
		entity.message = this.log.message;
		entity.payload = this.log.payload;

		return entity.persist().onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot store the {0}", this.log);
			return null;

		}).map(result -> result != null);
	}

}
