/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.nextPastTime;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;

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

		model.id = ValueGenerator.nextObjectId();
		model.enabled = flipCoin();
		model.createTimestamp = nextPastTime();
		model.updateTimestamp = nextPastTime();
		final var builder = new TopologyConnectionNodeTest();
		model.source = builder.nextModel();
		model.target = builder.nextModel();
	}

	/**
	 * Return the model from an entity.
	 *
	 * @param entity to get the model.
	 *
	 * @return the model from the entity.
	 */
	public static TopologyConnection from(TopologyConnectionEntity entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new TopologyConnection();
			model.id = entity.id;
			model.enabled = entity.enabled;
			model.createTimestamp = entity.createTimestamp;
			model.updateTimestamp = entity.updateTimestamp;
			if (entity.source != null) {

				model.source = TopologyConnectionNodeTest.from(entity.source);
			}
			if (entity.target != null) {

				model.target = TopologyConnectionNodeTest.from(entity.target);
			}

			return model;
		}
	}
}
