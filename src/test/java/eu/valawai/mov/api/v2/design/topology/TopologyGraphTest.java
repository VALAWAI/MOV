/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topology;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.api.v2.design.topologies.TopologyGraph;

/**
 * Test the {@link TopologyGraph}.
 *
 * @see TopologyGraph
 *
 * @author VALAWAI
 */
public class TopologyGraphTest extends ModelTestCase<TopologyGraph> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TopologyGraph createEmptyModel() {

		return new TopologyGraph();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(TopologyGraph model) {

		model.id = ValueGenerator.nextObjectId();
		model.name = ValueGenerator.nextPattern("Component name {0}");
		model.description = ValueGenerator.nextPattern("Component description {0}");

	}

}
