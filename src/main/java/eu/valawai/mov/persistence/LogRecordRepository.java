/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger.Level;

import eu.valawai.mov.api.v1.logs.LogRecord;
import eu.valawai.mov.api.v1.logs.LogRecordPage;
import io.quarkus.logging.Log;
import jakarta.inject.Singleton;

/**
 * The repository to manage the {@link LogRecord}'s.
 *
 * @see LogRecord
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Singleton
public class LogRecordRepository {

	/**
	 * The defined components.
	 */
	private static volatile List<LogRecord> LOGS = Collections.synchronizedList(new ArrayList<>());

	/**
	 * The number maximum of logs to store.
	 */
	@ConfigProperty(name = "valawai.logs.max", defaultValue = "10000")
	int maxLogs;

	/**
	 * Add a log.
	 *
	 * @param log to add.
	 *
	 * @return {@code true} if the log has been added.
	 */
	public boolean add(LogRecord log) {

		if (log != null) {

			if (log.payload != null) {

				Log.logv(Level.valueOf(log.level.name()), log.message);

			} else {

				Log.logv(Level.valueOf(log.level.name()), log.message + "\n{0}", log.payload);
			}

			synchronized (LOGS) {

				final var added = LOGS.add(log);
				for (var i = LOGS.size(); i > this.maxLogs; i--) {

					LOGS.remove(0);
				}
				return added;
			}

		} else {

			return false;
		}

	}

	/**
	 * Count the number of logs that contains the repository
	 *
	 * @return the number of logs that are stored on the repository.
	 */
	public int count() {

		synchronized (LOGS) {

			return LOGS.size();
		}

	}

	/**
	 * Obtain the last log record.
	 *
	 * @return the last record message or {@code null} if not record exists.
	 */
	public LogRecord last() {

		synchronized (LOGS) {

			final var index = LOGS.size() - 1;
			if (index > -1) {

				return LOGS.get(index);

			} else {

				return null;
			}
		}
	}

	/**
	 * Return the page with the logs that satisfy the parameters.
	 *
	 * @param pattern to match the logs message.
	 * @param level   to match the logs.
	 * @param order   to return the logs.
	 * @param offset  to the first log to return.
	 * @param limit   number maximum of logs to return.
	 *
	 * @return the page with the logs that satisfy the parameters.
	 */
	public LogRecordPage getLogRecordPage(String pattern, String level, String order, int offset, int limit) {

		final var page = new LogRecordPage();

		return page;
	}

}
