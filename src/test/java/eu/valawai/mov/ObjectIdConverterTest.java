/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.bson.types.ObjectId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the call that convert an Sting to a {@link ObjectId}.
 *
 * @see ObjectId
 * @see ObjectIdConverter
 *
 * @author VALAWAI
 */
public class ObjectIdConverterTest {

	/**
	 * The converter instance top use in the test.
	 */
	ObjectIdConverter converter = new ObjectIdConverter();

	/**
	 * Should not convert a bad value.
	 *
	 * @param value that can not be converted.
	 *
	 * @throws IllegalArgumentException because it try to convert a bad value
	 */
	@ParameterizedTest(name = "Should not convert the value {0}")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "1234567890abcdef", "1234567890abcdef1234567890abcdefg", "1234567890abcdeg",
			"1234567890abcde!", "123456789 0abcde" })
	public void shouldNotConvertBadValue(String value) throws IllegalArgumentException {

		assertThrows(IllegalArgumentException.class, () -> {

			this.converter.convert(value);
		});
	}

	/**
	 * Should convert a value.
	 *
	 * @param value to convert
	 */
	@ParameterizedTest(name = "Should  convert the value {0}")
	@ValueSource(strings = { "000000000000000000000000", "507f1f77bcf86cd799439011", "688cca9c7079a2f5e0f45ee1" })
	public void shouldConvertValue(String value) {

		final var id = this.converter.convert(value);
		assertEquals(value, id.toHexString(), "Conversion failed for " + value);

	}

}
