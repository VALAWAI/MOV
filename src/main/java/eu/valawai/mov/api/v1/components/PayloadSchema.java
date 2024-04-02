/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

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
		@Type(value = ArrayPayloadSchema.class, name = "ARRAY") })
public abstract class PayloadSchema extends Model {

	/**
	 * The payload type.
	 */
	@Schema(description = "The type of payload.")
	public PayloadType type;

	/**
	 * Check if this schema match another schema.
	 *
	 * @param other to check.
	 *
	 * @return {@code true} if this schema is equivalent to the other schema.
	 */
	public abstract boolean match(PayloadSchema other);

}
