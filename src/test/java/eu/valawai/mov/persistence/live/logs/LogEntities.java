/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.logs;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import eu.valawai.mov.api.v1.logs.LogRecordTest;
import eu.valawai.mov.persistence.live.components.ComponentEntities;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.logs.LogEntity;
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
		if (total < min) {

			nextLogs(min - total);
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

		final var components = ComponentEntities.minComponents(5);
		return nextLogs(num, components);

	}

	/**
	 * Create some log entities.
	 *
	 * @param num        number of logs to create.
	 * @param components to use on the logs.
	 *
	 * @return the created logs.
	 */
	public static List<LogEntity> nextLogs(long num, List<ComponentEntity> components) {

		final var logs = new ArrayList<LogEntity>();
		final var builder = new LogRecordTest();
		final var maxComponents = components.size();
		for (var i = 0; i < num; i++) {

			final var next = builder.nextModel();
			final LogEntity entity = new LogEntity();
			entity.level = next.level;
			entity.message = next.message;
			entity.payload = next.payload;
			entity.timestamp = next.timestamp;
			final var index = i % (maxComponents + 1);
			if (index < maxComponents) {

				entity.componentId = components.get(index).id;
			}
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
