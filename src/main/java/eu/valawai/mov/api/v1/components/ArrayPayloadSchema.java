/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

/**
 * A payload that is represented by an array of values.
 *
 * @see PayloadSchema
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(title = "A schema for a payload that contains an array of values.")
public class ArrayPayloadSchema extends PayloadSchema {

	/**
	 * The type for the elements on the array.
	 */
	@Schema(title = "The type for the array elements.")
	@NotNull
	public PayloadSchema items;

	/**
	 * Create a new array payload schema.
	 */
	public ArrayPayloadSchema() {

		this.type = PayloadType.ARRAY;
	}

}