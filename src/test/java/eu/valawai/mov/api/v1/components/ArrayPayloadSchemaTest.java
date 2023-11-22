/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

/**
 * Test the {@link ArrayPayloadSchema}.
 *
 * @see ArrayPayloadSchema
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ArrayPayloadSchemaTest extends PayloadSchemaTestCase<ArrayPayloadSchema> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayPayloadSchema createEmptyModel() {

		return new ArrayPayloadSchema();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayPayloadSchema nextModel() {

		final var model = this.createEmptyModel();
		model.items = nextPayloadSchema();
		return model;
	}

}
