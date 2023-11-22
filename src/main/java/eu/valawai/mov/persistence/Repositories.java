/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

/**
 * Common methods used on the repositories.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface Repositories {

	/**
	 * Check if the value is equals or match the regular expressions.
	 *
	 * @param pattern to check.
	 * @param value   to compare.
	 *
	 * @return {@code true} if the value match the pattern.
	 */
	public static boolean match(String pattern, String value) {

		if (pattern == null) {

			return true;

		} else if (pattern.startsWith("/")) {

			final var regex = "(?i)" + pattern.substring(1, pattern.length() - 1);
			return value.matches(regex);

		} else {

			return pattern.equalsIgnoreCase(value);
		}

	}
}
