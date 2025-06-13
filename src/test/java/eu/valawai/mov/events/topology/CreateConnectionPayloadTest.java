/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static eu.valawai.mov.ValueGenerator.flipCoin;

import eu.valawai.mov.events.PayloadTestCase;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;

/**
 * Test the {@link CreateConnectionPayload}.
 *
 * @see CreateConnectionPayload
 *
 * @author VALAWAI
 */
public class CreateConnectionPayloadTest extends PayloadTestCase<CreateConnectionPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CreateConnectionPayload createEmptyModel() {

		return new CreateConnectionPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(CreateConnectionPayload payload) {

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
	public static CreateConnectionPayload from(TopologyConnectionEntity entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new CreateConnectionPayload();
			model.source = NodePayloadTest.from(entity.source);
			model.target = NodePayloadTest.from(entity.target);
			model.enabled = entity.enabled;
			return model;

		}
	}

}
