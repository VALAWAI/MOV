/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.logs;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import eu.valawai.mov.api.v1.logs.LogRecordTest;
import io.quarkus.logging.Log;

/**
 * Methods used when test over LogEntity that has been stored.
 *
 * LogEntity
 *
 * @author VALAWAI
 */
public interface LogEntities {

	/**
	 * Check exist the minimum logs.
	 *
	 * @param min number of logs.
	 */
	public static void minLogs(int min) {

		final var total = LogEntity.count().await().atMost(Duration.ofSeconds(30));
		if (total > min) {

			nextLogs(total - min);
		}
	}

	/**
	 * Create some entities.
	 *
	 * @param num number of logs to create.
	 *
	 * @return the created logs.
	 */
	public static List<LogEntity> nextLogs(long num) {

		final var logs = new ArrayList<LogEntity>();
		for (var i = 0; i < num; i++) {

			final var next = new LogRecordTest().nextModel();
			final LogEntity entity = new LogEntity();
			entity.level = next.level;
			entity.message = next.message;
			entity.payload = next.payload;
			entity.timestamp = next.timestamp;
			final var stored = entity.persist().onFailure().recoverWithItem(error -> {

				Log.errorv(error, "Cannot persist {}", entity);
				return null;

			}).await().atMost(Duration.ofSeconds(30));
			if (stored == null) {

				fail("Cannot persist a component.");

			} else {

				logs.add(entity);
			}

		}

		return logs;

	}

}
