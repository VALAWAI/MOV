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

		this.fillIn(model, 3);
	}

	/**
	 * Create a new model.
	 *
	 * @param level max depth level to reach.
	 *
	 * @return the created model.
	 */
	public ObjectPayloadSchema nextModel(int level) {

		final var model = this.createEmptyModel();
		this.fillIn(model, level);
		return model;
	}

	/**
	 * Fill in a model with some random values.
	 *
	 * @param model to fill in.
	 * @param level max depth level to reach.
	 */
	public void fillIn(ObjectPayloadSchema model, int level) {

		final var max = rnd().nextInt(1, 7);
		for (var i = 0; i < max; i++) {

			final var name = nextPattern("property_name_{0}");
			final var type = nextPayloadSchema(level - 1);
			model.properties.put(name, type);
		}

	}

}
