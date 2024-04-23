/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.events.Payload;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import jakarta.validation.constraints.Pattern;

/**
 * The information of a component that is involved in a message.
 *
 * @see SentMessagePayload
 *
 * @author VALAWAI
 */
public class MinComponentPayload extends Payload {

	/**
	 * The identifier of the component.
	 */
	@BsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	@Pattern(regexp = "[0-9a-fA-F]{24}")
	public ObjectId id;

	/**
	 * The name of the component.
	 */
	public String name;

	/**
	 * The type level of the component in the VALAWAI.
	 */
	public ComponentType type;

}
