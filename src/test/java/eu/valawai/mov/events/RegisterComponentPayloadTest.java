/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextPattern;

/**
 * Test the {@link RegisterComponentPayload}.
 *
 * @see RegisterComponentPayload
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class RegisterComponentPayloadTest extends PayloadTestCase<RegisterComponentPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RegisterComponentPayload createEmptyPayload() {

		return new RegisterComponentPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RegisterComponentPayload nextPayload() {

		final var payload = this.createEmptyPayload();
		payload.type = next(ComponentType.values());
		payload.name = payload.type.name().toLowerCase() + nextPattern("_test_{0}");
		payload.version = nextPattern("{0}.{1}.{2}", 3);
		payload.api = new ComponentApiInfoTest().nextModel();
		return payload;
	}

}
