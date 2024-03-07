/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.ModelTestCase;
import io.vertx.core.json.Json;

/**
 * Generic test for the classes that extends the {@link PayloadSchema}.
 *
 * @see PayloadSchema
 *
 * @param <T> type of schema to test.
 *
 * @author VALAWAI
 */
public abstract class PayloadSchemaTestCase<T extends PayloadSchema> extends ModelTestCase<T> {

	/**
	 * Generate a next payload schema.
	 *
	 * @return the next payload schema.
	 */
	public static PayloadSchema nextPayloadSchema() {

		var option = 0;
		if (rnd().nextInt() % 3 == 0) {
			// only do 33% of times otherwise it create too big types
			option = rnd().nextInt(0, 4);
		}
		return switch (option) {
		case 0 -> new BasicPayloadSchemaTest().nextModel();
		case 1 -> new EnumPayloadSchemaTest().nextModel();
		case 2 -> new ObjectPayloadSchemaTest().nextModel();
		default -> new ArrayPayloadSchemaTest().nextModel();
		};
	}

	/**
	 * Should not match with {@code null} value.
	 */
	@Test
	public void shouldNotMatchWithNullValue() {

		final var model = this.nextModel();
		assertFalse(model.match(null));

	}

	/**
	 * Should not match with a different schema.
	 */
	@Test
	public void shouldNotMatchWithDifferentBasicPayloadSchema() {

		final var model1 = this.nextModel();
		var model2 = new BasicPayloadSchemaTest().nextModel();
		while (model1.equals(model2)) {

			model2 = new BasicPayloadSchemaTest().nextModel();
		}
		assertFalse(model1.match(model2));
		assertFalse(model2.match(model1));

	}

	/**
	 * Should not match with a different schema.
	 */
	@Test
	public void shouldNotMatchWithDifferentEnumPayloadSchema() {

		final var model1 = this.nextModel();
		var model2 = new EnumPayloadSchemaTest().nextModel();
		while (model1.equals(model2)) {

			model2 = new EnumPayloadSchemaTest().nextModel();
		}
		assertFalse(model1.match(model2));
		assertFalse(model2.match(model1));

	}

	/**
	 * Should not match with a different schema.
	 */
	@Test
	public void shouldNotMatchWithDifferentObjectPayloadSchema() {

		final var model1 = this.nextModel();
		var model2 = new ObjectPayloadSchemaTest().nextModel();
		while (model1.equals(model2)) {

			model2 = new ObjectPayloadSchemaTest().nextModel();
		}
		assertFalse(model1.match(model2));
		assertFalse(model2.match(model1));

	}

	/**
	 * Should not match with a different schema.
	 */
	@Test
	public void shouldNotMatchWithDifferentArrayPayloadSchema() {

		final var model1 = this.nextModel();
		var model2 = new ArrayPayloadSchemaTest().nextModel();
		while (model1.equals(model2)) {

			model2 = new ArrayPayloadSchemaTest().nextModel();
		}
		assertFalse(model1.match(model2));
		assertFalse(model2.match(model1));

	}

	/**
	 * Should match the same schema instance.
	 */
	@Test
	public void shouldMatchSameInstance() {

		final var model = this.nextModel();
		assertTrue(model.match(model));

	}

	/**
	 * Should match a model with the same model after encode and decode.
	 */
	@Test
	public void shouldMatch() {

		final var model1 = this.nextModel();
		final var json = Json.encode(model1);
		final var model2 = Json.decodeValue(json, model1.getClass());
		assertTrue(model1.match(model2));
		assertTrue(model2.match(model1));

	}

}
