/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextPattern;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;

/**
 * Test the {@link MinConnection}.
 *
 * @see MinConnection
 *
 * @author VALAWAI
 */
public class MinConnectionTest extends ModelTestCase<MinConnection> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MinConnection createEmptyModel() {

		return new MinConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(MinConnection model) {

		model.id = ValueGenerator.nextObjectId();
		model.source = "valaway/" + next(ComponentType.values()).name().toLowerCase() + nextPattern("_component_{0}");
		model.target = "valaway/" + next(ComponentType.values()).name().toLowerCase() + nextPattern("_component_{0}");
		model.enabled = flipCoin();

	}

	/**
	 * Return the model from an entity.
	 *
	 * @param entity to get the model.
	 *
	 * @return the model from the entity.
	 */
	public static MinConnection from(TopologyConnectionEntity entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new MinConnection();
			model.id = entity.id;
			if (entity.source != null) {

				model.source = entity.source.channelName;
			}
			if (entity.target != null) {

				model.target = entity.target.channelName;
			}
			model.enabled = entity.enabled;
			return model;
		}
	}
}
