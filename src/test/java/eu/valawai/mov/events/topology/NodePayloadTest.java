/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.events.PayloadTestCase;
import eu.valawai.mov.persistence.topology.TopologyNode;

/**
 * Test the {@link NodePayload}.
 *
 * @see NodePayload
 *
 * @author VALAWAI
 */
public class NodePayloadTest extends PayloadTestCase<NodePayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodePayload createEmptyModel() {

		return new NodePayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(NodePayload model) {

		model.componentId = ValueGenerator.nextObjectId();
		model.channelName = ValueGenerator.nextPattern("valawai/CX_source_name_{0}");
	}

	/**
	 * Return the model from the data of an entity.
	 *
	 * @param entity to get the data of the model.
	 *
	 * @return the model with the data of the entity.
	 */
	public static NodePayload from(TopologyNode entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new NodePayload();
			model.channelName = entity.channelName;
			model.componentId = entity.componentId;
			return model;
		}
	}

}
