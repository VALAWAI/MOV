/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.topology;

import static eu.valawai.mov.ValueGenerator.nextPastTime;
import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntities;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

/**
 * Methods to manage the {@like TopologyGraphEntity} over tests.
 *
 * @see TopologyGraphEntity
 *
 * @author VALAWAI
 */
public interface TopologyGraphEntities {

	/**
	 * Check exist the minimum topologies.
	 *
	 * @param min number of topologies.
	 *
	 * @return the min topologies.
	 */
	public static List<TopologyGraphEntity> minTopologies(int min) {

		final var total = TopologyGraphEntity.count().await().atMost(Duration.ofSeconds(30));
		if (total < min) {

			nextTopologyGraphs(min - total);
		}

		final Uni<List<TopologyGraphEntity>> find = TopologyGraphEntity.findAll(Sort.descending("_id")).range(0, min)
				.list();
		return find.await().atMost(Duration.ofSeconds(30));
	}

	/**
	 * Create a new topology.
	 *
	 * @return the created topology.
	 */
	public static TopologyGraphEntity nextTopologyGraph() {

		final TopologyGraphEntity entity = new TopologyGraphEntity();
		entity.name = nextPattern("Topology {0}");
		entity.description = "Description of '" + entity.name + "'.";
		entity.updatedAt = nextPastTime();
		entity.nodes = new ArrayList<>();
		final var components = ComponentDefinitionEntities.minComponents(5).subList(0, 5);
		for (final var component : components) {

			final var node = new TopologyGraphNode();
			node.componentRef = component.id;
			node.tag = "node_" + (entity.nodes.size() + 1);
			node.x = rnd().nextDouble(0.0d, 400.0d);
			node.y = rnd().nextDouble(0.0d, 400.0d);
			if (component.channels != null) {

				node.outputs = new ArrayList<>();
				for (final var channel : component.channels) {

					if (channel.publish != null) {

						final var index = rnd().nextInt(0, components.size());
						final var target = components.get(index);
						if (target.channels != null) {

							for (final var targetChannel : target.channels) {

								if (targetChannel.subscribe != null) {

									final var output = new TopologyGraphNodeOutputConnection();
									output.sourceChannel = channel.name;
									output.targetTag = "node_" + (index + 1);
									output.targetChannel = targetChannel.name;
									output.type = ValueGenerator.next(TopologyGraphConnectionType.values());
									node.outputs.add(output);
									break;
								}
							}
						}
					}
				}
			}
			entity.nodes.add(node);

		}

		final var stored = entity.persist().onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot persist {}", entity);
			return null;

		}).await().atMost(Duration.ofSeconds(30));
		if (stored == null) {

			fail("Cannot persist a topology.");
		}
		return entity;
	}

	/**
	 * Create some entities.
	 *
	 * @param num number of topologies to create.
	 *
	 * @return the created topologies.
	 */
	public static List<TopologyGraphEntity> nextTopologyGraphs(long num) {

		final var topologies = new ArrayList<TopologyGraphEntity>();
		for (var i = 0; i < num; i++) {

			final var next = nextTopologyGraph();
			topologies.add(next);

		}

		return topologies;

	}

	/**
	 * Create some topology entities until they are equals to the specified value.
	 *
	 * @param filter to count the topology entities that has to be created.
	 * @param num    number of topologies to create.
	 *
	 * @return the number of topologies entities that satisfy the filter.
	 */
	public static long nextTopologyGraphsUntil(Bson filter, long num) {

		var total = TopologyGraphEntity.mongoCollection().countDocuments(filter).onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot count the topology entities");
			return null;

		}).await().atMost(Duration.ofSeconds(30));
		while (total < num) {

			nextTopologyGraphs(num - total);
			total = TopologyGraphEntity.mongoCollection().countDocuments(filter).onFailure().recoverWithItem(error -> {

				Log.errorv(error, "Cannot count the topology entities");
				return null;

			}).await().atMost(Duration.ofSeconds(30));
		}

		return total;

	}

	/**
	 * Return an identifier for a topology that is not stored in the data base.
	 *
	 * @return the identifier of an undefined topology
	 */
	public static ObjectId undefined() {

		var id = ValueGenerator.nextObjectId();
		while (TopologyGraphEntity.findById(id).await().indefinitely() != null) {

			id = ValueGenerator.nextObjectId();
		}
		return id;
	}

}
