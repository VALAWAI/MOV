/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test the {@link Orders}.
 *
 * @see Orders
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class OrdersTest {

	/**
	 * Check it convert into the expected order.
	 *
	 * @param order    to convert.
	 * @param expected json sort.
	 */
	@ParameterizedTest(name = "Should {0} be encoded as {1}")
	@CsvSource(delimiter = ';', value = { ";{\"_id\": 1}", "name;{\"name\": 1, \"_id\": 1}",
			"+name;{\"name\": 1, \"_id\": 1}", "-name;{\"name\": -1, \"_id\": 1}",
			" name   , ,  description   ;{\"name\": 1, \"description\": 1, \"_id\": 1}",
			" -name   , -description   ;{\"name\": -1, \"description\": -1, \"_id\": 1}" })
	public void shouldOrderBy(String order, String expected) {

		final var sort = Orders.orderBy(order);
		assertNotNull(sort);
		final var json = Bsons.toString(sort);
		assertEquals(expected, json);

	}

}
