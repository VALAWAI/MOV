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

import java.util.HashMap;
import java.util.Map;

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
	 * @param max depth level.
	 *
	 * @return the next payload schema.
	 */
	public static PayloadSchema nextPayloadSchema(int max) {

		return nextPayloadSchema(max, new HashMap<>());
	}

	/**
	 * Generate a next payload schema.
	 *
	 * @param max        depth level.
	 * @param references that can be used.
	 *
	 * @return the next payload schema.
	 */
	public static PayloadSchema nextPayloadSchema(int max, Map<Integer, ObjectPayloadSchema> references) {

		if (max <= 0) {

			final var option = rnd().nextInt(4);
			return switch (option) {
			case 0 -> new BasicPayloadSchemaTest().nextModel();
			case 1 -> new ConstantPayloadSchemaTest().nextModel();
			case 2 -> ReferencePayloadSchemaTest.nextPayloadSchema(references);
			default -> new EnumPayloadSchemaTest().nextModel();
			};

		} else {

			final var option = rnd().nextInt(0, 9);
			return switch (option) {
			case 0 -> new BasicPayloadSchemaTest().nextModel();
			case 1 -> new ConstantPayloadSchemaTest().nextModel();
			case 2 -> ReferencePayloadSchemaTest.nextPayloadSchema(references);
			case 3 -> new EnumPayloadSchemaTest().nextModel();
			case 4 -> nextObjectPayloadSchema(max - 1, references);
			case 5 -> new ArrayPayloadSchemaTest().nextModel(max - 1, references);
			case 6 -> new OneOfPayloadSchemaTest().nextModel(max - 1, references);
			case 7 -> new AnyOfPayloadSchemaTest().nextModel(max - 1, references);
			default -> new AllOfPayloadSchemaTest().nextModel(max - 1, references);
			};
		}
	}

	/**
	 * Create an object schema.
	 *
	 * @param max        depth level.
	 * @param references that can be used.
	 *
	 * @return the next object schema.
	 */
	private static ObjectPayloadSchema nextObjectPayloadSchema(int max, Map<Integer, ObjectPayloadSchema> references) {

		final var object = new ObjectPayloadSchemaTest().nextModel(max, references);
		object.id = references.size() + 1;
		references.put(object.id, object);
		return object;

	}

	/**
	 * Should not match with {@code null} value.
	 */
	@Test
	public void shouldNotMatchWithNullValue() {

		final var model = this.nextModel();
		assertFalse(model.match(null, new HashMap<>()));

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
		assertFalse(model1.match(model2, new HashMap<>()));
		assertFalse(model2.match(model1, new HashMap<>()));

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
		assertFalse(model1.match(model2, new HashMap<>()));
		assertFalse(model2.match(model1, new HashMap<>()));

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
		assertFalse(model1.match(model2, new HashMap<>()));
		assertFalse(model2.match(model1, new HashMap<>()));

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
		assertFalse(model1.match(model2, new HashMap<>()));
		assertFalse(model2.match(model1, new HashMap<>()));

	}

	/**
	 * Should match the same schema instance.
	 */
	@Test
	public void shouldMatchSameInstance() {

		final var model = this.nextModel();
		assertTrue(model.match(model, new HashMap<>()));

	}

	/**
	 * Should match a model with the same model after encode and decode.
	 */
	@Test
	public void shouldMatch() {

		final var model1 = this.nextModel();
		final var json = Json.encode(model1);
		final var model2 = Json.decodeValue(json, model1.getClass());
		assertTrue(model1.match(model2, new HashMap<>()));
		assertTrue(model2.match(model1, new HashMap<>()));

	}

}
