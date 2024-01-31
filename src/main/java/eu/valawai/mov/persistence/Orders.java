/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import java.util.ArrayList;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Sorts;

/**
 * Utility methods to specify the order of a query.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface Orders {

	/**
	 * Return the sort
	 *
	 * @param order name of the field names separated by commas and start with plus
	 *              for ascending order or with minus with descending order.
	 *
	 * @return the order by docuemnts.
	 */
	public static Bson orderBy(String order) {

		final var sorts = new ArrayList<Bson>();
		if (order != null) {

			final var split = order.trim().split(",");
			for (var element : split) {

				element = element.trim();
				var type = 1;
				if (element.startsWith("+")) {

					element = element.substring(1).trim();

				} else if (element.startsWith("-")) {

					type = -1;
					element = element.substring(1).trim();
				}

				if (!element.isEmpty()) {

					if (type == 1) {

						sorts.add(Sorts.ascending(element));

					} else {

						sorts.add(Sorts.descending(element));
					}
				}
			}
		}
		sorts.add(Sorts.ascending("_id"));
		return Sorts.orderBy(sorts);
	}

}
