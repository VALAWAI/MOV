/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.components;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.Model;
import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntity;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdDeserializer;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * the definition of a component that can be used in a topology
 *
 * @see ComponentDefinitionEntity
 * @see ComponentEntity
 *
 * @author VALAWAI
 */
@Schema(title = "The definition of a component that can be used in a topology.")
public class ComponentDefinition extends Model {

	/**
	 * The identifier of the component.
	 */
	@Schema(description = "The identifier of the component", readOnly = true, example = "000000000000000000000000", implementation = String.class)
	@BsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public ObjectId id;

	/**
	 * The type of the component.
	 */
	@Schema(title = "The type of the component.")
	@NotNull
	public ComponentType type;

	/**
	 * The name of the component.
	 */
	@Schema(title = "The name of the component.")
	@NotEmpty
	public String name;

	/**
	 * The description of the component.
	 */
	@Schema(title = "The description of the component.")
	public String description;

	/**
	 * The documentation link of the component.
	 */
	@Schema(title = "The documentation link of the component.")
	@URL
	public String docsLink;

	/**
	 * The git link of the component.
	 */
	@Schema(title = "The url to the GitHub repository of the component.")
	@URL
	public String gitHubLink;

	/**
	 * The version of the component.
	 */
	@Schema(title = "The version of the component.")
	public VersionInfo version;

	/**
	 * The version of the API of the component.
	 */
	@Schema(title = "The API version of the component.")
	public VersionInfo apiVersion;

	/**
	 * The channels defined on the component.
	 */
	@Schema(description = "The channel associated to the component.")
	public List<ChannelSchema> channels;

}
