/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * A payload that is defined as a constant value.
 *
 * @see PayloadSchema
 *
 * @author VALAWAI
 */
@JsonRootName("const")
@Schema(description = "A payload that is defined as a constant value.")
public class ConstantPayloadSchema extends PayloadSchema {

	/**
	 * The constant value.
	 */
	@Schema(description = "The constant value.")
	public String value;

	/**
	 * Create a new enum payload schema.
	 */
	public ConstantPayloadSchema() {

		this.type = PayloadType.CONST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(PayloadSchema other) {

		return other instanceof final ConstantPayloadSchema schema
				&& (this.value == schema.value || this.value != null && this.value.equals(schema.value));
	}

}
