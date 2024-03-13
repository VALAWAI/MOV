/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.nextPastTime;

import eu.valawai.mov.events.PayloadTestCase;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;

/**
 * Test the {@link ConnectionPayload}.
 *
 * @see ConnectionPayload
 *
 * @author VALAWAI
 */
public class ConnectionPayloadTest extends PayloadTestCase<ConnectionPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionPayload createEmptyModel() {

		return new ConnectionPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ConnectionPayload payload) {

		payload.id = nextObjectId();
		payload.createTimestamp = nextPastTime();
		payload.updateTimestamp = nextPastTime();
		final var builder = new NodePayloadTest();
		payload.source = builder.nextModel();
		payload.target = builder.nextModel();
		payload.enabled = flipCoin();

	}

	/**
	 * Return the model from an entity.
	 *
	 * @param entity to get the model.
	 *
	 * @return the model from the entity.
	 */
	public static ConnectionPayload from(TopologyConnectionEntity entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new ConnectionPayload();
			model.id = entity.id;
			model.createTimestamp = entity.createTimestamp;
			model.updateTimestamp = entity.updateTimestamp;
			model.source = NodePayloadTest.from(entity.source);
			model.target = NodePayloadTest.from(entity.target);
			model.enabled = entity.enabled;
			return model;

		}
	}

}
