/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test the {@link Queries}.
 *
 * @see Queries
 *
 * @author VALAWAI
 */
public class QueriesTest {

	/**
	 * Check it convert a name and a value into a filter.
	 *
	 * @param name     of the filed.
	 * @param value    for the field.
	 * @param expected json sort.
	 */
	@ParameterizedTest(name = "Should {0} with {1} be encoded as {2}")
	@CsvSource(delimiter = ';', value = { "name;value;{\"name\": \"value\"}",
			"name;/.*value.*/;{\"name\": {\"$regularExpression\": {\"pattern\": \".*value.*\", \"options\": \"\"}}}",
			"name;/.*value.*/i;{\"name\": {\"$regularExpression\": {\"pattern\": \".*value.*\", \"options\": \"i\"}}}" })
	public void shouldFilterByValueOrRegexp(String name, String value, String expected) {

		final var filter = Queries.filterByValueOrRegexp(name, value);
		assertNotNull(filter);
		final var json = Bsons.toString(filter);
		assertEquals(expected, json);

	}

}
