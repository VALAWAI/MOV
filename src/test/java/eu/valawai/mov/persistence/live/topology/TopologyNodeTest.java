/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;

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

		model.componentId = ValueGenerator.nextObjectId();
		model.channelName = ValueGenerator.nextPattern("valawai/CX_source_name_{0}");
	}

}
