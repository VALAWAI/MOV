/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.connections;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.api.v1.components.MinComponentTest;
import eu.valawai.mov.persistence.live.topology.TopologyNode;

/**
 * Test the {@link LiveEndPoint}.
 *
 * @see LiveEndPoint
 *
 * @author VALAWAI
 */
public class LiveEndPointTest extends ModelTestCase<LiveEndPoint> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LiveEndPoint createEmptyModel() {

		return new LiveEndPoint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(LiveEndPoint model) {

		final var builder = new MinComponentTest();
		model.component = builder.nextModel();
		model.channelName = "valawai/" + model.component.type.name() + "/data/"
				+ ValueGenerator.nextPattern("channel_{0}");
	}

	/**
	 * Create the live end point form a model defined in the database.
	 *
	 * @param entity to get the data from.
	 *
	 * @return the model with the data of the entity.
	 */
	public static LiveEndPoint from(TopologyNode entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new LiveEndPoint();
			model.component = MinComponentTest.from(entity.componentId);
			model.channelName = entity.channelName;
			return model;

		}
	}

}
