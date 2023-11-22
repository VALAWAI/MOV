/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.next;

/**
 * Test the {@link BasicPayloadSchema}.
 *
 * @see BasicPayloadSchema
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class BasicPayloadSchemaTest extends PayloadSchemaTestCase<BasicPayloadSchema> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicPayloadSchema createEmptyModel() {

		return new BasicPayloadSchema();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicPayloadSchema nextModel() {

		final var model = this.createEmptyModel();
		model.format = next(BasicPayloadFormat.values());
		return model;
	}

}
