/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.rnd;

/**
 * Test the {@link ObjectPayloadSchema}.
 *
 * @see ObjectPayloadSchema
 *
 * @author VALAWAI
 */
public class ObjectPayloadSchemaTest extends PayloadSchemaTestCase<ObjectPayloadSchema> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectPayloadSchema createEmptyModel() {

		return new ObjectPayloadSchema();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ObjectPayloadSchema model) {

		final var max = rnd().nextInt(1, 7);
		for (var i = 0; i < max; i++) {

			final var name = next("property_name_{0}");
			final var type = nextPayloadSchema();
			model.properties.put(name, type);
		}

	}

}
