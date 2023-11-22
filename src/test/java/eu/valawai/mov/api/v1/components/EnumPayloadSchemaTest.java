/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.rnd;

/**
 * Test the {@link EnumPayloadSchema}.
 *
 * @see EnumPayloadSchema
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class EnumPayloadSchemaTest extends PayloadSchemaTestCase<EnumPayloadSchema> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EnumPayloadSchema createEmptyModel() {

		return new EnumPayloadSchema();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EnumPayloadSchema nextModel() {

		final var model = this.createEmptyModel();
		final var max = rnd().nextInt(2, 11);
		for (var i = 0; i < max; i++) {

			final var value = next("enum_value_{0}");
			model.values.add(value);
		}

		return model;
	}

}
