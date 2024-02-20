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

import org.bson.conversions.Bson;

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
	 * Create some log entities.
	 *
	 * @param num number of logs to create.
	 *
	 * @return the created logs.
	 */
	public static List<LogEntity> nextLogs(long num) {

		final var logs = new ArrayList<LogEntity>();
		final var builder = new LogRecordTest();
		for (var i = 0; i < num; i++) {

			final var next = builder.nextModel();
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

	/**
	 * Create some log entities until they are equals to the specified value.
	 *
	 * @param filter to count the log entities that has to be created.
	 * @param num    number of logs to create.
	 *
	 * @return the number of logs entities that satisfy the filter.
	 */
	public static long nextLogsUntil(Bson filter, long num) {

		var total = LogEntity.mongoCollection().countDocuments(filter).onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot count the log entities");
			return null;

		}).await().atMost(Duration.ofSeconds(30));
		while (total < num) {

			LogEntities.nextLogs(num - total);
			total = LogEntity.mongoCollection().countDocuments(filter).onFailure().recoverWithItem(error -> {

				Log.errorv(error, "Cannot count the log entities");
				return null;

			}).await().atMost(Duration.ofSeconds(30));
		}

		return total;

	}

}
