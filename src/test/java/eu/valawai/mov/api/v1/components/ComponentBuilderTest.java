/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.vertx.core.json.Json;

/**
 * Test the {@link ComponentBuilder}.
 *
 * @see ComponentBuilder
 *
 * @author VALAWAI
 */
public class ComponentBuilderTest {

	/**
	 * Check that create a component from a AsyncAPI specification.
	 *
	 * @param index of the file to test.
	 *
	 * @throws IOException If cannot read the files to compare.
	 */
	@ParameterizedTest(name = "Should create a component for the file {0}")
	@ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7 })
	public void shouldCrreateComponentFor(int index) throws IOException {

		final var yaml = new String(this.getClass().getClassLoader()
				.getResourceAsStream("eu/valawai/mov/api/v1/components/test" + index + ".asyncapi.yml").readAllBytes());
		final var json = new String(this.getClass().getClassLoader()
				.getResourceAsStream("eu/valawai/mov/api/v1/components/test" + index + ".component.json")
				.readAllBytes());
		final var expected = Json.decodeValue(json, Component.class);
		final var component = ComponentBuilder.fromAsyncapi(yaml);
		assertEquals(expected, component);

	}

	/**
	 * Check that not create a component from a bad value.
	 *
	 * @param value that not contains a component
	 */
	@ParameterizedTest(name = "Should not create a component with the value {0}")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "undefined", "\tvalue:\n\t\t1", "asyncapi: 2.6.0", "asyncapi: 2.6.0\n channels:",
			"asyncapi: 2.6.0\n channels:\n  test:" })
	public void shouldNotCrreateComponentForBadValue(String value) {

		final var component = ComponentBuilder.fromAsyncapi(value);
		assertNull(component);

	}

}
