/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.Model;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotEmpty;

/**
 * The minimal information of a component.
 *
 * @author VALAWAI
 */
public class MinComponent extends Model {

	/**
	 * The identifier of the component.
	 */
	@Schema(description = "The identifier of the component", readOnly = true, example = "000000000000000000000000", implementation = String.class)
	@BsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;

	/**
	 * The name of the component.
	 */
	@Schema(description = "The component name.")
	@NotEmpty
	public String name;

	/**
	 * The description of the component.
	 */
	@Schema(description = "The component description.")
	public String description;

	/**
	 * The type of component.
	 */
	@Schema(description = "The component type.")
	@NotNull
	public ComponentType type;

}
