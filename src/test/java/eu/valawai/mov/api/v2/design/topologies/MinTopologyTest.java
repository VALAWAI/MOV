/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import eu.valawai.mov.persistence.design.topology.TopologyGraphEntity;

/**
 * Test the {@link MinTopology}.
 *
 * @see MinTopology
 *
 * @author VALAWAI
 */
public class MinTopologyTest extends MinTopologyTestCase<MinTopology> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MinTopology createEmptyModel() {

		return new MinTopology();
	}

	/**
	 * Return a {@link MinTopology} with the date define din a
	 * {@link TopologyGraphEntity}.
	 *
	 * @param entity to get the data.
	 *
	 * @return the modle with the data of teh entity.
	 */
	public static MinTopology from(TopologyGraphEntity entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new MinTopology();
			MinTopologyTestCase.fillInWith(model, entity);
			return model;
		}
	}

}
