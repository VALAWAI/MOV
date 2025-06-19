/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link TopologyConnectionEndpoint}.
 *
 * @see TopologyConnectionEndpoint
 *
 * @author VALAWAI
 */
public class TopologyConnectionEndpointTest extends ModelTestCase<TopologyConnectionEndpoint> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TopologyConnectionEndpoint createEmptyModel() {

		return new TopologyConnectionEndpoint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(TopologyConnectionEndpoint model) {

		model.nodeTag = ValueGenerator.nextPattern("node_{0}");
		model.channel = ValueGenerator.nextPattern("valawai/C0/dummy/data/model_{0}");
	}

}
