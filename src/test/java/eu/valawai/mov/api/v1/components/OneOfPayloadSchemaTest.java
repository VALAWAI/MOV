/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

/**
 * Test the {@link OneOfPayloadSchema}.
 *
 * @see OneOfPayloadSchema
 *
 * @author VALAWAI
 */
public class OneOfPayloadSchemaTest extends DiversePayloadSchemaTestCase<OneOfPayloadSchema> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OneOfPayloadSchema createEmptyModel() {

		return new OneOfPayloadSchema();
	}

}
