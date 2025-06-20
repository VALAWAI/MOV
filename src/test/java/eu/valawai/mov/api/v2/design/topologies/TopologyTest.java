/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import java.util.ArrayList;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.persistence.design.topology.TopologyGraphEntity;

/**
 * Test the {@link Topology}.
 *
 * @see Topology
 *
 * @author VALAWAI
 */
public class TopologyTest extends MinTopologyTestCase<Topology> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Topology createEmptyModel() {

		return new Topology();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(Topology model) {

		super.fillIn(model);

		final var maxNodes = ValueGenerator.rnd().nextInt(0, 10);
		if (maxNodes > 0) {

			final var builder = new TopologyNodeTest();
			model.nodes = new ArrayList<>();
			for (var i = 0; i < maxNodes; i++) {

				final var node = builder.nextModel();
				model.nodes.add(node);
			}

		}

		final var maxConnections = ValueGenerator.rnd().nextInt(0, 10);
		if (maxConnections > 0) {

			final var builder = new TopologyConnectionTest();
			model.connections = new ArrayList<>();
			for (var i = 0; i < maxConnections; i++) {

				final var connection = builder.nextModel();
				model.connections.add(connection);
			}

		}
		model.updatedAt = ValueGenerator.nextPastTime();

	}

	/**
	 * Return the topology from the entity.
	 *
	 * @param entity to get the data for the model.
	 *
	 * @return the topology model with the data defined in the entity.
	 */
	public static Topology from(TopologyGraphEntity entity) {

		if (entity == null) {
			return null;

		} else {

			final var model = new Topology();
			model.id = entity.id;
			model.name = entity.name;
			model.description = entity.description;
			model.updatedAt = entity.updatedAt;
			if (entity.nodes != null) {

				model.nodes = new ArrayList<>();
				model.connections = new ArrayList<>();
				for (final var node : entity.nodes) {

					final var modelNode = TopologyNodeTest.from(node);
					model.nodes.add(modelNode);
					if (node.outputs != null) {

						for (final var output : node.outputs) {

							final var connection = TopologyConnectionTest.from(node, output);
							model.connections.add(connection);
						}
					}
				}
			}

			return model;

		}
	}

}
