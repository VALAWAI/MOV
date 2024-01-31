/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

/**
 * The utilities to create queries.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface Queries {

	/**
	 * Create the filter for a filed.
	 *
	 * @param name  of the filed.
	 * @param value for the field.
	 *
	 * @return the filter for the field.
	 */
	public static Bson filterByValueOrRegexp(String name, String value) {

		if (value.matches("/.+/.*")) {

			var index = value.lastIndexOf('/');
			final var regexp = value.substring(1, index);
			index++;
			if (value.length() > index) {

				final var options = value.substring(index);
				return Filters.regex(name, regexp, options);

			} else {

				return Filters.regex(name, regexp);
			}

		} else {

			return Filters.eq(name, value);
		}

	}

}
