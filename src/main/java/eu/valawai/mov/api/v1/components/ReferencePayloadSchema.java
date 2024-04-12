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
 * A payload that is the reference to a previous payload.
 *
 * @see PayloadSchema
 *
 * @author VALAWAI
 */
@JsonRootName("ref")
@Schema(description = "A payload that ia a reference to a previous payload.")
public class ReferencePayloadSchema extends PayloadSchema {

	/**
	 * The identifier of the schema that it refers.
	 */
	@Schema(description = "The identifier of the schema that it refers.")
	public int identifier;

	/**
	 * Create a new reference payload schema.
	 */
	public ReferencePayloadSchema() {

		this.type = PayloadType.REF;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(PayloadSchema other) {

		return other instanceof final ReferencePayloadSchema schema && this.identifier == schema.identifier;
	}

}
