/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.component;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
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
import eu.valawai.mov.api.v2.design.components.VersionInfoTest;
import eu.valawai.mov.services.GitHubRepositoryTest;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

/**
 * Methods to manage the {@like ComponentDefinitionEntity} over tests.
 *
 * @author VALAWAI
 */
public interface ComponentDefinitionEntities {

	/**
	 * Check exist the minimum components.
	 *
	 * @param min number of components.
	 *
	 * @return the min components.
	 */
	public static List<ComponentDefinitionEntity> minComponents(int min) {

		final var total = ComponentDefinitionEntity.count().await().atMost(Duration.ofSeconds(30));
		if (total < min) {

			nextComponentDefinitions(min - total);
		}

		final Uni<List<ComponentDefinitionEntity>> find = ComponentDefinitionEntity.findAll(Sort.descending("_id"))
				.range(0, min).list();
		return find.await().atMost(Duration.ofSeconds(30));
	}

	/**
	 * Create a new component.
	 *
	 * @return the created component.
	 */
	public static ComponentDefinitionEntity nextComponentDefinition() {

		final ComponentDefinitionEntity entity = new ComponentDefinitionEntity();
		entity.type = next(ComponentType.values());
		entity.name = nextPattern("Component {0}");
		entity.description = "Description of '" + entity.name + "'.";
		final var normalizedName = entity.name.toLowerCase().replaceAll("\\W", "_");
		entity.docsLink = "https://valawai.github.io/docs/components/" + entity.type.name() + "/" + normalizedName;
		entity.repository = new GitHubRepositoryTest().nextModel();
		final var versionBuilder = new VersionInfoTest();
		entity.version = versionBuilder.nextModel();
		entity.apiVersion = versionBuilder.nextModel();
		final var max = ValueGenerator.rnd().nextInt(0, 5);
		if (max > 0) {

			entity.channels = new ArrayList<>();
			for (var i = 0; i < max; i++) {

				final var channel = new ChannelSchema();
				final var action = nextPattern("action_{0}");
				channel.name = "valawai/" + entity.type.name().toLowerCase() + "/" + normalizedName + "/"
						+ next(Arrays.asList("control", "data")) + "/" + action;
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
	public static List<ComponentDefinitionEntity> nextComponentDefinitions(long num) {

		final var components = new ArrayList<ComponentDefinitionEntity>();
		for (var i = 0; i < num; i++) {

			final var next = nextComponentDefinition();
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
	public static long nextComponentDefinitionsUntil(Bson filter, long num) {

		var total = ComponentDefinitionEntity.mongoCollection().countDocuments(filter).onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot count the component entities");
					return null;

				}).await().atMost(Duration.ofSeconds(30));
		while (total < num) {

			nextComponentDefinitions(num - total);
			total = ComponentDefinitionEntity.mongoCollection().countDocuments(filter).onFailure()
					.recoverWithItem(error -> {

						Log.errorv(error, "Cannot count the component entities");
						return null;

					}).await().atMost(Duration.ofSeconds(30));
		}

		return total;

	}

	/**
	 * Return the component associated to the identifier.
	 *
	 * @param id identifier of the component to get.
	 *
	 * @return the component associated to the identifier or {@code null} if any
	 *         component is defined.
	 */
	public static ComponentDefinitionEntity getById(ObjectId id) {

		final Uni<ComponentDefinitionEntity> find = ComponentDefinitionEntity.findById(id);
		return find.await().atMost(Duration.ofSeconds(30));
	}

	/**
	 * Return the number of components defined in the database.
	 *
	 * @return the number of components.
	 */
	public static long count() {

		final Uni<Long> count = ComponentDefinitionEntity.count();
		return count.await().atMost(Duration.ofSeconds(30));
	}

	/**
	 * Return the update time of the oldest component.
	 *
	 * @return the oldest update timestamp.
	 */
	public static long oldestComponentTimestamp() {

		final Uni<ComponentDefinitionEntity> find = ComponentDefinitionEntity.findAll(Sort.ascending("updatedAt"))
				.firstResult();
		return find.map(c -> c == null ? 0 : c.updatedAt).await().atMost(Duration.ofSeconds(30));
	}

	/**
	 * Return the update time of the newest component.
	 *
	 * @return the newest update timestamp.
	 */
	public static long newestComponentTimestamp() {

		final Uni<ComponentDefinitionEntity> find = ComponentDefinitionEntity.findAll(Sort.descending("updatedAt"))
				.firstResult();
		return find.map(c -> c == null ? 0 : c.updatedAt).await().atMost(Duration.ofSeconds(30));
	}

}
