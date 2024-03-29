/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence;

import java.util.Arrays;

import org.bson.conversions.Bson;

/**
 * Methods to manage {@link Bson}.
 *
 * @see Bson
 *
 * @author VALAWAI
 */
public interface Bsons {

	/**
	 * Convert a {@link Bson} into a json string.
	 *
	 * @param bson to convert into a string.
	 *
	 * @return the JSON codification of the {@link Bson}.
	 */
	public static String toString(Bson bson) {

		if (bson == null) {

			return "null";

		} else {

			return bson.toBsonDocument().toJson();
		}
	}

	/**
	 * Convert some {@link Bson}s into a json string.
	 *
	 * @param bsons to convert into a string.
	 *
	 * @return the JSON codification of the {@link Bson}s.
	 */
	public static String toString(Bson... bsons) {

		if (bsons == null) {

			return "null";

		} else {

			return toString(Arrays.asList(bsons));
		}
	}

	/**
	 * Convert an iterable collection of {@link Bson}s into a json string.
	 *
	 * @param bsons collection to convert into a string.
	 *
	 * @return the JSON codification of the collection.
	 */
	public static String toString(Iterable<Bson> bsons) {

		if (bsons == null) {

			return "null";

		} else {

			final var builder = new StringBuilder();
			builder.append("[");
			var first = true;
			for (final var bson : bsons) {

				if (first) {

					first = false;

				} else {

					builder.append(",");
				}
				builder.append(toString(bson));
			}
			builder.append("]");
			return builder.toString();

		}
	}

}
