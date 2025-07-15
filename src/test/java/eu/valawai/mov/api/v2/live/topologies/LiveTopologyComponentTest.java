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

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.components.AbstractMinComponentTestCase;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

/**
 * Test the {@link LiveTopologyComponent}.
 *
 * @see LiveTopologyComponent
 *
 * @author VALAWAI
 */
public class LiveTopologyComponentTest extends AbstractMinComponentTestCase<LiveTopologyComponent> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LiveTopologyComponent createEmptyModel() {

		return new LiveTopologyComponent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(LiveTopologyComponent model) {

		super.fillIn(model);
		final var max = ValueGenerator.rnd().nextInt(0, 10);
		if (max > 0) {

			final var builder = new LiveTopologyComponentOutConnectionTest();
			model.connections = new ArrayList<>();
			for (var i = 0; i < max; i++) {

				final var connection = builder.nextModel();
				model.connections.add(connection);
			}
		}

	}

	/**
	 * Return the model from the entity.
	 *
	 * @param entity to get the data for the model.
	 *
	 * @return the model with the data of the entity.
	 */
	public static LiveTopologyComponent from(ComponentEntity entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new LiveTopologyComponent();
			AbstractMinComponentTestCase.fillWith(model, entity);
			final ReactiveMongoCollection<TopologyConnectionEntity> collection = TopologyConnectionEntity
					.mongoCollection();
			final Uni<List<TopologyConnectionEntity>> find = collection
					.find(Filters.and(Filters.eq("source.componentId", entity.id), Filters
							.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null))))
					.collect().asList();
			final var connectionEntities = find.subscribe().withSubscriber(UniAssertSubscriber.create())
					.awaitItem(Duration.ofSeconds(30)).getItem();
			if (connectionEntities != null && !connectionEntities.isEmpty()) {

				model.connections = new ArrayList<>();
				for (final var connectionEntity : connectionEntities) {

					final var connection = LiveTopologyComponentOutConnectionTest.from(connectionEntity);
					model.connections.add(connection);
				}
			}

			return model;

		}
	}
}
