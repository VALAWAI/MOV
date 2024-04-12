/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

/**
 * Test the {@link AnyOfPayloadSchema}.
 *
 * @see AnyOfPayloadSchema
 *
 * @author VALAWAI
 */
public class AnyOfPayloadSchemaTest extends DiversePayloadSchemaTestCase<AnyOfPayloadSchema> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnyOfPayloadSchema createEmptyModel() {

		return new AnyOfPayloadSchema();
	}

}
