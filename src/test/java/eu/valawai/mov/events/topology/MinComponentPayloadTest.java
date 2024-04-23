/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.nextPattern;

import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.events.PayloadTestCase;

/**
 * Test the {@link MinComponentPayload}.
 *
 * @see MinComponentPayload
 *
 * @author VALAWAI
 */
public class MinComponentPayloadTest extends PayloadTestCase<MinComponentPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MinComponentPayload createEmptyModel() {

		return new MinComponentPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(MinComponentPayload payload) {

		payload.id = nextObjectId();
		payload.type = next(ComponentType.values());
		payload.name = nextPattern(payload.type.name().toLowerCase() + "_test_component_name_{0}");

	}

}
