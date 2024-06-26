/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import eu.valawai.mov.api.Model;

/**
 * The description of a payload of a message send or received from
 * {@link ChannelSchema}.
 *
 * @see ChannelSchema
 * @see BasicPayloadSchema
 * @see EnumPayloadSchema
 * @see ObjectPayloadSchema
 * @see ArrayPayloadSchema
 *
 * @author VALAWAI
 */
@Schema(description = "A schema that define the possible payload.")
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = BasicPayloadSchema.class, name = "BASIC"),
		@Type(value = EnumPayloadSchema.class, name = "ENUM"),
		@Type(value = ObjectPayloadSchema.class, name = "OBJECT"),
		@Type(value = ArrayPayloadSchema.class, name = "ARRAY"),
		@Type(value = ConstantPayloadSchema.class, name = "CONST"),
		@Type(value = ReferencePayloadSchema.class, name = "REF"),
		@Type(value = OneOfPayloadSchema.class, name = "ONE_OF"),
		@Type(value = AnyOfPayloadSchema.class, name = "ANY_OF"),
		@Type(value = AllOfPayloadSchema.class, name = "ALL_OF") })
public abstract class PayloadSchema extends Model {

	/**
	 * The payload type.
	 */
	@Schema(description = "The type of payload.")
	public PayloadType type;

	/**
	 * Check if this schema match another schema.
	 *
	 * @param other      to check.
	 * @param references to other schemas.
	 *
	 * @return {@code true} if this schema is equivalent to the other schema.
	 */
	public boolean match(PayloadSchema other, Map<Integer, PayloadSchema> references) {

		if (other == null) {

			return false;

		} else if (other instanceof final ReferencePayloadSchema ref) {

			final var reference = references.get(ref.identifier);
			if (reference == null) {
				// reference not found.
				return false;

			} else {

				return this.match(reference, references);
			}

		} else {

			if (other instanceof final ObjectPayloadSchema object && object.id != null) {

				if (!references.containsKey(object.id)) {

					references.put(object.id, object);

				} else if (!references.get(object.id).matchPayload(other, references)) {

					return false;
				}
			}

			return this.matchPayload(other, references);
		}

	}

	/**
	 * Check if this schema match another schema.
	 *
	 * @param other      to check.
	 * @param references to other schemas.
	 *
	 * @return {@code true} if this schema is equivalent to the other schema.
	 */
	protected abstract boolean matchPayload(PayloadSchema other, Map<Integer, PayloadSchema> references);

}
