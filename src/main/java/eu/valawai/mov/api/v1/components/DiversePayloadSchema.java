/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotEmpty;

/**
 * A payload schema that is defined a a set of different types.
 *
 * @see PayloadSchema
 *
 * @author VALAWAI
 */
@Schema(description = "A schema for a payload that contains an array of values.")
public abstract class DiversePayloadSchema extends PayloadSchema {

	/**
	 * The possible types that can be used on the schema.
	 */
	@Schema(description = "The possible types that can be used on this schema.")
	@NotEmpty
	public List<PayloadSchema> items;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(PayloadSchema other) {

		if (other instanceof final DiversePayloadSchema diverse) {

			if (this.items == diverse.items || this.items != null && this.items.isEmpty() && diverse.items == null
					|| this.items == null && diverse.items != null && diverse.items.isEmpty()) {
				// match without items
				return true;

			} else if (this.items != null && diverse.items != null && this.items.size() == diverse.items.size()) {
				// check without take the order
				final var copy = new ArrayList<>(this.items);
				copy.removeAll(diverse.items);
				return copy.isEmpty();
			}
		}

		return false;

	}

}
