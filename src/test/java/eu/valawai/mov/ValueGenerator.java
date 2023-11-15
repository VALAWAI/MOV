/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Used to generate values for the tests.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ValueGenerator {

	/**
	 * Return the current version of the generator.
	 */
	private static final Random CURRENT = new Random(0);

	/**
	 * Create a new value generator.
	 */
	private ValueGenerator() {
	}

	/**
	 * Return the current generator.
	 *
	 * @return the current value generator.
	 */
	public static final Random rnd() {

		return CURRENT;
	}

	/**
	 * Return the next enumeration value to use.
	 *
	 * @param values to select the next value.
	 * @param <E>    the enumeration type that the values are defined.
	 *
	 * @return the next random enumeration value.
	 *
	 */
	public static final <E> E next(final E[] values) {

		final var index = CURRENT.nextInt(values.length);
		return values[index];

	}

	/**
	 * Return the next value from a string array.
	 *
	 * @param values to select the next value.
	 *
	 * @return the next random string value.
	 */
	public static final String next(final String... values) {

		final var index = CURRENT.nextInt(values.length);
		return values[index];

	}

	/**
	 * Return the next value from a list.
	 *
	 * @param values to select the next value.
	 * @param <E>    the element type that the values are defined.
	 *
	 * @return the next random element value.
	 *
	 */
	public static final <E> E next(final List<E> values) {

		final var index = CURRENT.nextInt(values.size());
		return values.get(index);

	}

	/**
	 * Return the next pattern with new values.
	 *
	 * @param pattern to instantiate.
	 * @param max     number maximum of values to replace.
	 *
	 * @return the pattern that has been replaced.
	 */
	public static String nextPattern(String pattern, int max) {

		final var args = new Object[max];
		for (var i = 0; i < max; i++) {

			final var value = CURRENT.nextInt(0, 99999999);
			args[i] = String.format("%09d", value);

		}
		return MessageFormat.format(pattern, args);

	}

	/**
	 * Return the next single pattern.
	 *
	 * @param pattern to instantiate.
	 *
	 * @return the pattern that has been replaced.
	 */
	public static String nextPattern(String pattern) {

		return nextPattern(pattern, 1);

	}

	/**
	 * Return the next UUID.
	 *
	 * @return the next random UUID.
	 */
	public static UUID nextUUID() {

		return new UUID(CURRENT.nextLong(), CURRENT.nextLong());

	}

}