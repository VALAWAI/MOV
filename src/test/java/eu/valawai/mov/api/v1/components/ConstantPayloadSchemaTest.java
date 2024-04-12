/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.nextPattern;

/**
 * Test the {@link ConstantPayloadSchema}.
 *
 * @see ConstantPayloadSchema
 *
 * @author VALAWAI
 */
public class ConstantPayloadSchemaTest extends PayloadSchemaTestCase<ConstantPayloadSchema> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConstantPayloadSchema createEmptyModel() {

		return new ConstantPayloadSchema();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ConstantPayloadSchema model) {

		model.value = nextPattern("constant_value_{0}");
	}

}
