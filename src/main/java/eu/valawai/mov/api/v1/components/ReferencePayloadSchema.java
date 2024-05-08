/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.util.Map;

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
	protected boolean matchPayload(PayloadSchema other, Map<Integer, PayloadSchema> references) {

		if (other instanceof final ReferencePayloadSchema ref) {

			return this.identifier == ref.identifier;

		} else {

			final var reference = references.get(this.identifier);
			if (reference == null) {
				// reference not found.
				return false;

			} else {

				return reference.match(other, references);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(PayloadSchema other, Map<Integer, PayloadSchema> references) {

		if (other instanceof final ReferencePayloadSchema ref) {

			return this.identifier == ref.identifier;

		} else {

			return super.match(other, references);
		}
	}

}
