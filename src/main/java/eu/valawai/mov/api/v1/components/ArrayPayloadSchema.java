/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

/**
 * A payload that is represented by an array of values.
 *
 * @see PayloadSchema
 *
 * @author VALAWAI
 */
@Schema(description = "A schema for a payload that contains an array of values.")
public class ArrayPayloadSchema extends PayloadSchema {

	/**
	 * The type for the elements on the array.
	 */
	@Schema(description = "The type for the array elements.")
	@NotNull
	public PayloadSchema items;

	/**
	 * Create a new array payload schema.
	 */
	public ArrayPayloadSchema() {

		this.type = PayloadType.ARRAY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(PayloadSchema other) {

		return other instanceof final ArrayPayloadSchema array
				&& (this.items == array.items || this.items != null && this.items.match(array.items));
	}

}
