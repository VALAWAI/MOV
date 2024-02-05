/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.Model;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

/**
 * The name of the component.
 *
 * @author VALAWAI
 */
@Schema(title = "A VALAWAI component.")
public class Component extends Model {

	/**
	 * The identifier of the component.
	 */
	@Schema(title = "The identifier of the component", readOnly = true, example = "000000000000000000000000", implementation = String.class)
	@BsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;

	/**
	 * The name of the component.
	 */
	@Schema(title = "The component name.")
	@NotEmpty
	public String name;

	/**
	 * The description of the component.
	 */
	@Schema(title = "The component description.")
	public String description;

	/**
	 * The version of the components.
	 */
	@Schema(title = "The component version.")
	@NotEmpty
	@Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
	public String version;

	/**
	 * The version of the API.
	 */
	@Schema(title = "The component API version.")
	@NotEmpty
	@Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
	public String apiVersion;

	/**
	 * The type of component.
	 */
	@Schema(title = "The component type.")
	@NotNull
	public ComponentType type;

	/**
	 * The time when the component is registered.
	 */
	@Schema(title = "The time when the component is registered. The epoch time in seconds when the component is registered", readOnly = true)
	public long since;

	/**
	 * The channels defined on the component.
	 */
	@Schema(title = "The channel associated to the component.")
	public List<ChannelSchema> channels;

}
