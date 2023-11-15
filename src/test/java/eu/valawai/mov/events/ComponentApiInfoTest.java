/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import static eu.valawai.mov.ValueGenerator.nextPattern;

/**
 * Test the {@link ComponentApiInfo}.
 *
 * @see ComponentApiInfo
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ComponentApiInfoTest extends PayloadTestCase<ComponentApiInfo> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComponentApiInfo createEmptyPayload() {

		return new ComponentApiInfo();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComponentApiInfo nextPayload() {

		final var payload = this.createEmptyPayload();
		payload.version = nextPattern("{0}.{1}.{2}", 3);
		payload.yaml = nextPattern("""
				asyncapi: 2.6.0
				info:
				  title: Hello world application
				  version: '{0}.{1}.{2}'
				channels:
				  hello:
				    publish:
				      message:
				        payload:
				          type: string
				          pattern: '^hello .+$'""", 3).trim();
		return payload;
	}

}
