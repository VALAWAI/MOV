/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.vertx.core.json.Json;

/**
 * Tests over the classes that extends the {2link DiversePayloadSchema}.
 *
 * @param <T> type of schema to test.
 *
 * @author VALAWAI
 */
public abstract class DiversePayloadSchemaTestCase<T extends DiversePayloadSchema> extends PayloadSchemaTestCase<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(T model) {

		this.fillIn(model, 3, new HashMap<>());
	}

	/**
	 * Create a new model.
	 *
	 * @param level      max depth level to reach.
	 * @param references that can be used.
	 *
	 * @return the created model.
	 */
	public T nextModel(int level, Map<Integer, ObjectPayloadSchema> references) {

		final var model = this.createEmptyModel();
		this.fillIn(model, level, references);
		return model;
	}

	/**
	 * Fill in a model with some random values.
	 *
	 * @param model      to fill in.
	 * @param level      max depth level to reach.
	 * @param references that can be used.
	 */
	public void fillIn(T model, int level, Map<Integer, ObjectPayloadSchema> references) {

		final var max = rnd().nextInt(1, 5);
		model.items = new ArrayList<>();
		for (var i = 0; i < max; i++) {

			final var item = nextPayloadSchema(level - 1, references);
			model.items.add(item);
		}
	}

	/**
	 * Should match to empty payloads.
	 */
	@Test
	public void shouldMatchToEmptyPayloads() {

		final var source = this.createEmptyModel();
		final var target = this.createEmptyModel();

		assertTrue(source.match(target, new HashMap<>()));
		assertTrue(target.match(source, new HashMap<>()));

		source.items = new ArrayList<>();
		assertTrue(source.match(target, new HashMap<>()));
		assertTrue(target.match(source, new HashMap<>()));

		target.items = new ArrayList<>();
		assertTrue(source.match(target, new HashMap<>()));
		assertTrue(target.match(source, new HashMap<>()));

	}

	/**
	 * Should match with different order.
	 */
	@Test
	public void shouldMatchToWithDiferentOrder() {

		final var model1 = this.nextModel();
		final var json = Json.encode(model1);
		final var model2 = Json.decodeValue(json, model1.getClass());
		Collections.shuffle(model2.items, rnd());
		assertTrue(model1.match(model2, new HashMap<>()));
		assertTrue(model2.match(model1, new HashMap<>()));
	}

}
