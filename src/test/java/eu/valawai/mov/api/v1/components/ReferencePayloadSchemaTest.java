/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.rnd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import eu.valawai.mov.ValueGenerator;

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

	/**
	 * Create a next reference schema.
	 *
	 * @param references that can be used.
	 *
	 * @return the created reference.
	 */
	public static PayloadSchema nextPayloadSchema(Map<Integer, ObjectPayloadSchema> references) {

		if (references == null || references.isEmpty()) {
			// can not use a reference that is not created
			return PayloadSchemaTestCase.nextPayloadSchema(0, references);

		} else {

			final var model = new ReferencePayloadSchema();
			final var ids = new ArrayList<>(references.keySet());
			Collections.shuffle(ids, ValueGenerator.rnd());
			model.identifier = ids.get(0);
			return model;

		}
	}

}
