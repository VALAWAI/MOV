/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import static eu.valawai.mov.persistence.Repositories.match;

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

				final var add = match(pattern, log.message) && match(level, log.level.name());
				if (add) {

					page.logs.add(log);
				}

			}
		}
		page.total = page.logs.size();
		if (order != null) {

			final var factors = order.split("\\s*,\\s*");
			page.logs.sort((log1, log2) -> {

				var cmp = 0;
				for (final var factor : factors) {

					switch (factor) {
					case "timestamp":
					case "+timestamp":
						cmp = Long.compare(log1.timestamp, log2.timestamp);
						break;
					case "-timestamp":
						cmp = Long.compare(log2.timestamp, log1.timestamp);
						break;
					case "message":
					case "+message":
						cmp = log1.message.compareTo(log2.message);
						break;
					case "-message":
						cmp = log2.message.compareTo(log1.message);
						break;
					case "level":
					case "+level":
						cmp = log1.level.compareTo(log2.level);
						break;
					case "-level":
						cmp = log2.level.compareTo(log1.level);
						break;
					}
					if (cmp != 0) {
						break;
					}
				}
				return cmp;
			});
		}

		final var max = page.logs.size();
		if (offset >= max) {

			page.logs = null;

		} else {

			page.logs = page.logs.subList(offset, Math.min(offset + limit, max));

		}
		return page;
	}

}
