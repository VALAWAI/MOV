/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import java.util.ArrayList;

import eu.valawai.mov.ValueGenerator;

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

	}

}
