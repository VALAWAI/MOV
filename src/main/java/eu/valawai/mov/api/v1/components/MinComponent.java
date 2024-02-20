/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
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
 * @author UDT-IA, IIIA-CSIC
 */
public class MinComponent extends Model {

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
	 * The type of component.
	 */
	@Schema(title = "The component type.")
	@NotNull
	public ComponentType type;

}
