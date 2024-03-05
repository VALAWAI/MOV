/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.ValueGenerator.rnd;

/**
 * Test the {@link EnumPayloadSchema}.
 *
 * @see EnumPayloadSchema
 *
 * @author VALAWAI
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
	public void fillIn(EnumPayloadSchema model) {

		final var max = rnd().nextInt(2, 11);
		for (var i = 0; i < max; i++) {

			final var value = nextPattern("enum_value_{0}");
			model.values.add(value);
		}
	}

}
