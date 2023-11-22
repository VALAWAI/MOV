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
 * Test the {@link ObjectPayloadSchema}.
 *
 * @see ObjectPayloadSchema
 *
 * @author UDT-IA, IIIA-CSIC
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
	public ObjectPayloadSchema nextModel() {

		final var model = this.createEmptyModel();
		final var max = rnd().nextInt(1, 7);
		for (var i = 0; i < max; i++) {

			final var name = next("property_name_{0}");
			final var type = nextPayloadSchema();
			model.properties.put(name, type);
		}

		return model;
	}

}
