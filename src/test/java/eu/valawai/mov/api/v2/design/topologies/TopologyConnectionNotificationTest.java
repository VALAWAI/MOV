/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.persistence.design.topology.TopologyGraphConnectionNotification;
import eu.valawai.mov.persistence.design.topology.TopologyGraphConnectionType;

/**
 * Test the {@link TopologyConnectionNotification}.
 *
 * @see TopologyConnectionNotification
 *
 * @author VALAWAI
 */
public class TopologyConnectionNotificationTest extends ModelTestCase<TopologyConnectionNotification> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TopologyConnectionNotification createEmptyModel() {

		return new TopologyConnectionNotification();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(TopologyConnectionNotification model) {

		model.target = new TopologyConnectionEndpointTest().nextModel();
		model.convertCode = ValueGenerator.nextPattern("//Convert {0}");
		model.type = ValueGenerator.next(TopologyGraphConnectionType.values());
	}

	/**
	 * Return the model from an entity.
	 *
	 * @param entity to convert to a model.
	 *
	 * @return the model with the data of the entity.
	 */
	public static TopologyConnectionNotification from(TopologyGraphConnectionNotification entity) {

		final var model = new TopologyConnectionNotification();
		if (entity.targetTag != null || entity.targetChannel != null) {

			model.target = new TopologyConnectionEndpoint();
			model.target.nodeTag = entity.targetTag;
			model.target.channel = entity.targetChannel;
		}

		model.convertCode = entity.convertCode;
		model.type = entity.type;
		return model;
	}

}
