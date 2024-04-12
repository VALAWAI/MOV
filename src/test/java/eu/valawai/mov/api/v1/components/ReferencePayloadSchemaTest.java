/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.rnd;

/**
 * Test the {@link ReferencePayloadSchema}.
 *
 * @see ReferencePayloadSchema
 *
 * @author VALAWAI
 */
public class ReferencePayloadSchemaTest extends PayloadSchemaTestCase<ReferencePayloadSchema> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReferencePayloadSchema createEmptyModel() {

		return new ReferencePayloadSchema();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ReferencePayloadSchema model) {

		model.identifier = rnd().nextInt(0, 10000000);
	}

}
