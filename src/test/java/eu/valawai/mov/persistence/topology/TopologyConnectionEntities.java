/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.nextPastTime;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import eu.valawai.mov.api.v1.components.ChannelSchemaTest;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v1.components.PayloadSchema;
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
	 * Create a new topology connection.
	 *
	 * @return the created connection.
	 */
	public static TopologyConnectionEntity nextTopologyConnection() {

		final var subsriptionsCount = rnd().nextInt(0, 4);
		return nextTopologyConnection(subsriptionsCount);
	}

	/**
	 * Create a new topology connection.
	 *
	 * @param subsriptionsCount the number of subscriptions for the connection.
	 *
	 * @return the created connection.
	 */
	public static TopologyConnectionEntity nextTopologyConnection(int subsriptionsCount) {

		final TopologyConnectionEntity entity = new TopologyConnectionEntity();
		entity.createTimestamp = nextPastTime();
		entity.updateTimestamp = nextPastTime();
		entity.enabled = flipCoin();
		entity.source = new TopologyNode();
		entity.target = new TopologyNode();

		PayloadSchema publish = null;
		while (entity.source.componentId == null) {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null && channel.subscribe == null) {

						entity.source.componentId = component.id;
						entity.source.channelName = channel.name;
						publish = channel.publish;
						break;
					}
				}
			}

		}

		while (entity.target.componentId == null) {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null && channel.publish == null) {

						entity.target.componentId = component.id;
						entity.target.channelName = channel.name;
						channel.subscribe = publish;
						component.update().await().atMost(Duration.ofSeconds(30));
						break;
					}
				}
			}

		}

		if (subsriptionsCount > 0) {

			entity.c2Subscriptions = new ArrayList<>();
			do {

				final var component = ComponentEntities.nextComponent();
				if (component.type == ComponentType.C2) {

					if (component.channels == null) {

						component.channels = new ArrayList<>();
					}

					final var channel = new ChannelSchemaTest().nextModel();
					channel.subscribe = null;
					channel.publish = publish;
					component.channels.add(channel);
					final var stored = component.update().onFailure().recoverWithItem(error -> {

						Log.errorv(error, "Cannot update {0}", component);
						return null;

					}).await().atMost(Duration.ofSeconds(30));
					if (stored == null) {

						fail("Cannot persist a component subscription.");
					}

					final var subscription = new TopologyNode();
					subscription.componentId = component.id;
					subscription.channelName = channel.name;
					entity.c2Subscriptions.add(subscription);
				}

			} while (entity.c2Subscriptions.size() < subsriptionsCount);
		}

		final var stored = entity.persist().onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot persist {0}", entity);
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
