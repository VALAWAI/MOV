/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.topologies;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import io.quarkus.mongodb.FindOptions;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

/**
 * Test the {@link LiveTopology}.
 *
 * @see LiveTopology
 *
 * @author VALAWAI
 */
public class LiveTopologyTest extends ModelTestCase<LiveTopology> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LiveTopology createEmptyModel() {

		return new LiveTopology();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(LiveTopology model) {

		final var max = ValueGenerator.rnd().nextInt(1, 10);
		final var builder = new LiveTopologyComponentTest();
		model.components = new ArrayList<>();
		for (var i = 0; i < max; i++) {

			final var node = builder.nextModel();
			model.components.add(node);
		}
	}

	/**
	 * Return the current live topology.
	 *
	 * @param offset the index of the first live topology component to return.
	 * @param limit  the maximum number of live topology components to return.
	 *
	 * @return the current live topology.
	 */
	public static LiveTopology current(int offset, int limit) {

		final var model = new LiveTopology();

		final ReactiveMongoCollection<ComponentEntity> collection = ComponentEntity.mongoCollection();
		final var query = Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null));
		final var options = new FindOptions().skip(offset).limit(limit).sort(Sorts.ascending("_id"));
		final Uni<List<ComponentEntity>> find = collection.find(query, options).collect().asList();
		final var entities = find.subscribe().withSubscriber(UniAssertSubscriber.create())
				.awaitItem(Duration.ofSeconds(30)).getItem();
		if (entities != null && !entities.isEmpty()) {

			model.components = new ArrayList<>();
			for (final var entity : entities) {

				final var component = LiveTopologyComponentTest.from(entity);
				model.components.add(component);
			}
			model.components.sort((c1, c2) -> c1.id.compareTo(c2.id));
		}
		return model;

	}

	/**
	 * Sort the topology to can be compared.
	 *
	 * @param topology to sort.
	 */
	public static void sort(LiveTopology topology) {

		if (topology != null && topology.components != null) {

			topology.components.sort((c1, c2) -> c1.id.compareTo(c2.id));
			for (final var component : topology.components) {

				if (component.connections != null) {

					component.connections.sort((c1, c2) -> {
						var cmp = c1.id.compareTo(c2.id);
						if (cmp == 0) {

							cmp = c1.channel.compareTo(c2.channel);
						}
						return cmp;
					});

					for (final var connection : component.connections) {

						if (connection.notifications != null) {

							connection.notifications.sort((c1, c2) -> {
								var cmp = c1.id.compareTo(c2.id);
								if (cmp == 0) {

									cmp = c1.channel.compareTo(c2.channel);
								}
								return cmp;
							});

						}
					}
				}

			}
		}
	}

}
