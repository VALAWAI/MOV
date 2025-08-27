/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v1.components.PayloadSchema;
import eu.valawai.mov.api.v1.components.PayloadSchemaTestCase;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

/**
 * Methods to manage the {@like ComponentEntity} over tests.
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
	 *
	 * @return the min components.
	 */
	public static List<ComponentEntity> minComponents(int min) {

		final var total = ComponentEntity.count().await().atMost(Duration.ofSeconds(30));
		if (total < min) {

			nextComponents(min - total);
		}

		final Uni<List<ComponentEntity>> find = ComponentEntity.findAll(Sort.descending("_id")).range(0, min).list();
		return find.await().atMost(Duration.ofSeconds(30));
	}

	/**
	 * Create a new component.
	 *
	 * @return the created component.
	 */
	public static ComponentEntity nextComponent() {

		final ComponentEntity entity = new ComponentEntity();
		entity.type = next(ComponentType.values());
		final var name = nextPattern("component_{0}");
		entity.name = "valawai/" + entity.type.name().toLowerCase() + "/" + name;
		entity.description = "Description of " + name;
		entity.version = nextPattern("{0}.{1}.{2}", 3);
		entity.apiVersion = nextPattern("{0}.{1}.{2}", 3);
		entity.since = rnd().nextLong(0, Instant.now().getEpochSecond());
		final var max = ValueGenerator.rnd().nextInt(0, 5);
		if (max > 0) {

			entity.channels = new ArrayList<>();
			for (var i = 0; i < max; i++) {

				final var channel = new ChannelSchema();
				final var action = nextPattern("action_{0}");
				channel.name = entity.name + "/" + next(Arrays.asList("control", "data")) + "/" + action;
				channel.description = "Description of " + action;
				final PayloadSchema schema = PayloadSchemaTestCase.nextPayloadSchema(3);
				if (rnd().nextBoolean()) {

					channel.publish = schema;

				} else {

					channel.subscribe = schema;
				}
				entity.channels.add(channel);
			}
		}
		final var stored = entity.persist().onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot persist {}", entity);
			return null;

		}).await().atMost(Duration.ofSeconds(30));
		if (stored == null) {

			fail("Cannot persist a component.");
		}
		return entity;
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

			final var next = nextComponent();
			components.add(next);

		}

		return components;

	}

	/**
	 * Create some component entities until they are equals to the specified value.
	 *
	 * @param filter to count the component entities that has to be created.
	 * @param num    number of components to create.
	 *
	 * @return the number of components entities that satisfy the filter.
	 */
	public static long nextComponentsUntil(Bson filter, long num) {

		var total = ComponentEntity.mongoCollection().countDocuments(filter).onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot count the component entities");
			return null;

		}).await().atMost(Duration.ofSeconds(30));
		while (total < num) {

			ComponentEntities.nextComponents(num - total);
			total = ComponentEntity.mongoCollection().countDocuments(filter).onFailure().recoverWithItem(error -> {

				Log.errorv(error, "Cannot count the component entities");
				return null;

			}).await().atMost(Duration.ofSeconds(30));
		}

		return total;

	}

	/**
	 * Remove all the component entities.
	 */
	public static void clear() {

		ComponentEntity.deleteAll().await().atMost(Duration.ofSeconds(30));

	}

	/**
	 * Return an identifier for a component that is not stored in the data base.
	 *
	 * @return the identifier of an undefined component.
	 */
	public static ObjectId undefined() {

		var id = ValueGenerator.nextObjectId();
		while (ComponentEntity.findById(id).await().indefinitely() != null) {

			id = ValueGenerator.nextObjectId();
		}
		return id;
	}

}
