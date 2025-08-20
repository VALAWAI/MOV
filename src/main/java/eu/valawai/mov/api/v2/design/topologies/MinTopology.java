/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.Model;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdDeserializer;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;

/**
 * Represents the minimal information required to define a topology between
 * VALAWAI components for the execution of a value-aware application.
 *
 * @author VALAWAI Team
 * @version 1.0.0
 */
@Schema(description = "Represents the minimal information of a topology between VALAWAI components to run a value-aware application.")
public class MinTopology extends Model {

	/**
	 * The unique identifier of the topology.
	 */
	@Schema(description = "The unique identifier of the topology. This is system-generated and read-only.", readOnly = true, examples = "688cca9c7079a2f5e0f45ee1", implementation = String.class)
	@BsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	@Nullable
	public ObjectId id;

	/**
	 * The human-readable name assigned to this topology. This field is mandatory
	 * and helps identify the topology within the system.
	 */
	@Schema(title = "The name of the topology.", description = "A unique, human-readable name for the topology. This field is mandatory.")
	@NotEmpty(message = "Topology name cannot be empty.")
	public String name;

	/**
	 * An optional detailed description providing more context or purpose for the
	 * topology. This can include information about its function, design, or
	 * intended use.
	 */
	@Schema(title = "The description of the topology.", description = "An optional, detailed description of the topology's purpose or design.")
	public String description;

}