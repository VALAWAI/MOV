/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.events.Payload;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * The description of a component that is in VALAWAI.
 *
 * @author VALAWAI
 */
@JsonRootName("component_payload")
public class ComponentPayload extends Payload {

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
	 * The description of the component.
	 */
	public String description;

	/**
	 * The version of the component.
	 */
	@Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
	public String version;

	/**
	 * The version of the component API.
	 */
	@Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
	@JsonProperty("api_version")
	public String apiVersion;

	/**
	 * The type level of the component in the VALAWAI.
	 */
	public ComponentType type;

	/**
	 * The epoch time, in seconds, since the component is available in VALAWAI.
	 */
	@Min(0)
	public long since;

	/**
	 * The channels that the component have.
	 */
	public List<ChannelSchema> channels;

}
