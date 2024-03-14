/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.next;

/**
 * Test the {@link BasicPayloadSchema}.
 *
 * @see BasicPayloadSchema
 *
 * @author VALAWAI
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
	public void fillIn(BasicPayloadSchema model) {

		model.format = next(BasicPayloadFormat.values());
	}

}
