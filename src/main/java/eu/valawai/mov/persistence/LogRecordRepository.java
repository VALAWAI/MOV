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

import eu.valawai.mov.TimeManager;
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

			log.timestamp = TimeManager.now();
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
	 * Clear all the logs.
	 */
	public void clear() {

		synchronized (LOGS) {

			LOGS.clear();
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
	 * Obtain the first log record.
	 *
	 * @return the first record message or {@code null} if not record exists.
	 */
	public LogRecord first() {

		synchronized (LOGS) {

			if (LOGS.isEmpty()) {

				return null;

			} else {

				return LOGS.get(0);
			}
		}
	}

	/**
	 * Return the page with the logs that satisfy the parameters.
	 *
	 * @param pattern to match the logs message or payload.
	 * @param level   to match the logs.
	 * @param order   to return the logs.
	 * @param offset  to the first log to return.
	 * @param limit   number maximum of logs to return.
	 *
	 * @return the page with the logs that satisfy the parameters.
	 */
	public LogRecordPage getLogRecordPage(String pattern, String level, String order, int offset, int limit) {

		final var page = new LogRecordPage();
		page.offset = offset;
		page.logs = new ArrayList<>();
		synchronized (LOGS) {

			for (final var log : LOGS) {

				final var add = this.match(pattern, log.message) && this.match(level, log.level.name());
				if (add) {

					page.logs.add(log);
				}

			}
		}
		if (order != null) {

			final var factors = order.split("\\w*,\\w*");
			page.logs.sort((log1, log2) -> {

				final var cmp = 0;
				for (final var factor : factors) {

					if (cmp != 0) {
						break;
					}
				}
				return cmp;
			});
		}

		final var max = page.logs.size();
		if (max == 0 || offset > max) {

			page.logs = null;

		} else {

			page.logs = page.logs.subList(offset, Math.min(offset + limit, max));

		}
		return page;
	}

	/**
	 * Check if the value is equals or match the regular expressions.
	 *
	 * @param pattern to check.
	 * @param value   to compare.
	 *
	 * @return {@code true} if the value match the pattern.
	 */
	private boolean match(String pattern, String value) {

		if (pattern == null) {

			return true;

		} else if (pattern.startsWith("/")) {

			final var regex = "(?i)" + pattern.substring(1, pattern.length() - 1);
			return value.matches(regex);

		} else {

			return pattern.equalsIgnoreCase(value);
		}

	}

}
