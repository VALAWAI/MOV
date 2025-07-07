/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.topology;

import java.util.ArrayList;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v2.design.topologies.Topology;
import eu.valawai.mov.persistence.AbstractEntityOperator;
import io.smallrye.mutiny.Uni;

/**
 * Update the {@link Topology}.
 *
 * @see Topology
 * @see TopologyGraphEntity
 *
 * @author VALAWAI
 */
public class UpdateTopology extends AbstractEntityOperator<Boolean, UpdateTopology> {

	/**
	 * The identifier of the topology to update.
	 */
	protected ObjectId id;

	/**
	 * The data with the data to update the topology.
	 */
	protected Topology topology;

	/**
	 * Create the operator.
	 */
	private UpdateTopology() {

	}

	/**
	 * Create the operator to update a {@link Topology}.
	 *
	 * @return the operator to get the topology.
	 */
	public static UpdateTopology fresh() {

		return new UpdateTopology();

	}

	/**
	 * Specify the identifier of the topology to get.
	 *
	 * @param id identifier of the topology to get.
	 *
	 * @return the operator to get the topology.
	 */
	public UpdateTopology withId(final ObjectId id) {

		this.id = id;
		return this;
	}

	/**
	 * Specify the topology data to update the {@link TopologyGraphEntity}.
	 *
	 * @param topology to update the entity.
	 *
	 * @return the operator to update the topology.
	 */
	public UpdateTopology withTopology(final Topology topology) {

		this.topology = topology;
		return this;
	}

	/**
	 * Update a {@link TopologyGraphEntity} with the data of a {@link Topology}.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public Uni<Boolean> execute() {

		this.topology.id = this.id;
		this.topology.updatedAt = TimeManager.now();
		final var filter = Filters.eq("_id", this.id);
		final var graphNodes = new ArrayList<TopologyGraphNode>();
		if (this.topology.nodes != null) {

			for (final var node : this.topology.nodes) {

				final var graphNode = new TopologyGraphNode();
				graphNode.tag = node.tag;
				graphNode.x = node.position.x;
				graphNode.y = node.position.y;
				if (node.component != null) {

					graphNode.componentRef = node.component.id;
				}

				if (this.topology.connections != null) {

					graphNode.outputs = new ArrayList<>();
					for (final var connection : this.topology.connections) {

						if (node.tag.equals(connection.source.nodeTag)) {

							final var output = new TopologyGraphNodeOutputConnection();
							output.sourceChannel = connection.source.channel;
							output.targetTag = connection.target.nodeTag;
							output.targetChannel = connection.target.channel;
							output.convertCode = connection.convertCode;
							output.type = connection.type;
							graphNode.outputs.add(output);
						}
					}
				}

				graphNodes.add(graphNode);
			}
		}
		final var update = Updates.combine(Updates.set("name", this.topology.name),
				Updates.set("description", this.topology.description),
				Updates.set("updatedAt", this.topology.updatedAt), Updates.set("nodes", graphNodes));

		return TopologyGraphEntity.mongoCollection().updateOne(filter, update)
				.map(updated -> updated != null && updated.getModifiedCount() > 0);

	}

}
