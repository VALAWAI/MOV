/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import static eu.valawai.mov.ValueGenerator.nextUUID;
import static eu.valawai.mov.ValueGenerator.rnd;

import java.util.ArrayList;

import eu.valawai.mov.events.PayloadTestCase;

/**
 * Test the {@link ComponentsPagePayload}.
 *
 * @see ComponentsPagePayload
 *
 * @author VALAWAI
 */
public class ComponentsPagePayloadTest extends PayloadTestCase<ComponentsPagePayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComponentsPagePayload createEmptyModel() {

		return new ComponentsPagePayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ComponentsPagePayload payload) {

		payload.queryId = nextUUID().toString();
		payload.total = rnd().nextInt(0, 1001);
		final int max = rnd().nextInt(0, 10);
		if (max > 0) {

			payload.components = new ArrayList<>();
			final var builder = new ComponentPayloadTest();
			for (var i = 0; i < max; i++) {

				final var component = builder.nextModel();
				payload.components.add(component);

			}
		}

	}

}
