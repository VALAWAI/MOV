/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.nextPastTime;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import eu.valawai.mov.persistence.components.ComponentEntities;
import io.quarkus.logging.Log;

/**
 * Utility calss to manage the {@link TopologyConnectionEntity} used on tests.
 *
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
public interface TopologyConnectionEntities {

	/**
	 * Create a new component.
	 *
	 * @return the created component.
	 */
	public static TopologyConnectionEntity nextTopologyConnection() {

		final TopologyConnectionEntity entity = new TopologyConnectionEntity();
		entity.createTimestamp = nextPastTime();
		entity.updateTimestamp = nextPastTime();
		entity.enabled = flipCoin();
		entity.source = new TopologyNode();
		entity.target = new TopologyNode();

		while (entity.source.componentId == null) {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null) {

						entity.source.componentId = component.id;
						entity.source.channelName = channel.id;
						break;
					}
				}
			}

		}

		while (entity.target.componentId == null) {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						entity.target.componentId = component.id;
						entity.target.channelName = channel.id;
						break;
					}
				}
			}

		}

		final var stored = entity.persist().onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot persist {}", entity);
			return null;

		}).await().atMost(Duration.ofSeconds(30));
		if (stored == null) {

			fail("Cannot persist a topology connection.");
		}
		return entity;
	}

	/**
	 * Check exist the minimum topology connections.
	 *
	 * @param min number of topology connections.
	 */
	public static void minTopologyConnections(int min) {

		final var total = TopologyConnectionEntity.count().await().atMost(Duration.ofSeconds(30));
		if (total < min) {

			nextTopologyConnections(min - total);
		}
	}

	/**
	 * Create some entities.
	 *
	 * @param num number of topology connections to create.
	 *
	 * @return the created topology connections.
	 */
	public static List<TopologyConnectionEntity> nextTopologyConnections(long num) {

		final var components = new ArrayList<TopologyConnectionEntity>();
		for (var i = 0; i < num; i++) {

			final var next = nextTopologyConnection();
			components.add(next);

		}

		return components;

	}

	/**
	 * Create some topology connection entities until they are equals to the
	 * specified value.
	 *
	 * @param filter to count the topology connection entities that has to be
	 *               created.
	 * @param num    number of topology connections to create.
	 *
	 * @return the number of topology connection entities that satisfy the filter.
	 */
	public static long nextTopologyConnectionsUntil(Bson filter, long num) {

		var total = TopologyConnectionEntity.mongoCollection().countDocuments(filter).onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot count the component entities");
					return null;

				}).await().atMost(Duration.ofSeconds(30));
		while (total < num) {

			nextTopologyConnections(num - total);
			total = TopologyConnectionEntity.mongoCollection().countDocuments(filter).onFailure()
					.recoverWithItem(error -> {

						Log.errorv(error, "Cannot count the component entities");
						return null;

					}).await().atMost(Duration.ofSeconds(30));
		}

		return total;

	}

}
