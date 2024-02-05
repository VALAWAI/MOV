/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.components;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import eu.valawai.mov.api.v1.components.ComponentTest;
import io.quarkus.logging.Log;

/**
 * Methods used when test over ComponentEntity that has been stored.
 *
 * ComponentEntity
 *
 * @author VALAWAI
 */
public interface ComponentEntities {

	/**
	 * Check exist the minimum components.
	 *
	 * @param min number of components.
	 */
	public static void minComponents(int min) {

		final var total = ComponentEntity.count().await().atMost(Duration.ofSeconds(30));
		if (total > min) {

			nextComponents(total - min);
		}
	}

	/**
	 * Create some entities.
	 *
	 * @param num number of components to create.
	 *
	 * @return the created components.
	 */
	public static List<ComponentEntity> nextComponents(long num) {

		final var components = new ArrayList<ComponentEntity>();
		for (var i = 0; i < num; i++) {

			final var next = new ComponentTest().nextModel();
			final ComponentEntity entity = new ComponentEntity();
			entity.apiVersion = next.apiVersion;
			entity.channels = next.channels;
			entity.description = next.description;
			entity.name = next.name;
			entity.since = next.since;
			entity.type = next.type;
			entity.version = next.version;
			final var stored = entity.persist().onFailure().recoverWithItem(error -> {

				Log.errorv(error, "Cannot persist {}", entity);
				return null;

			}).await().atMost(Duration.ofSeconds(30));
			if (stored == null) {

				fail("Cannot persist a component.");

			} else {

				components.add(entity);
			}

		}

		return components;

	}

}
