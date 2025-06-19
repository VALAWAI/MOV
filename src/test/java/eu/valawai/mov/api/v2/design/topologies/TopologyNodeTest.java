/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.api.v2.design.components.ComponentDefinitionTest;

/**
 * Test the {@link TopologyNode}.
 *
 * @see TopologyNode
 *
 * @author VALAWAI
 */
public class TopologyNodeTest extends ModelTestCase<TopologyNode> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TopologyNode createEmptyModel() {

		return new TopologyNode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(TopologyNode model) {

		model.id = ValueGenerator.nextPattern("node_{0}");
		model.position = new PointTest().nextModel();
		model.component = new ComponentDefinitionTest().nextModel();
	}

}
