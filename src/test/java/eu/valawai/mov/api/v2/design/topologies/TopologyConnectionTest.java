/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link TopologyConnection}.
 *
 * @see TopologyConnection
 *
 * @author VALAWAI
 */
public class TopologyConnectionTest extends ModelTestCase<TopologyConnection> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TopologyConnection createEmptyModel() {

		return new TopologyConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(TopologyConnection model) {

		final var builder = new TopologyConnectionEndpointTest();
		model.source = builder.nextModel();
		model.target = builder.nextModel();
	}

}
