/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence.topology;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link TopologyNode}.
 *
 * @see TopologyNode
 *
 * @author UDT-IA, IIIA-CSIC
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
	protected void fillIn(TopologyNode model) {

		model.componentId = ValueGenerator.nextObjectId();
		model.channelName = ValueGenerator.nextPattern("valawai/CX_source_name_{0}");
	}

}
