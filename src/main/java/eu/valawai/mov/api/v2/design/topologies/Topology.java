/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import java.util.List;

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
 * Represents a logical topology definition of the components that define the
 * interaction between the VALAWAI component.
 *
 * @see TopologyNode
 * @see TopologyConnection
 *
 * @author VALAWAI
 */
@Schema(description = "The definition of a topology with a graph.")
public class Topology extends Model {

	/**
	 * The unique identifier of the topology.
	 */
	@Schema(description = "The unique identifier of the topology. This is system-generated and read-only.", readOnly = true, example = "60c87a5e8b4e7c001a1b2c3d", implementation = String.class)
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

	/**
	 * A list of {@link TopologyNode} objects that represent the different VALAWAI
	 * components that form the value aware application.
	 */
	@Schema(title = "The nodes defined within the topology.")
	public List<TopologyNode> nodes;

	/**
	 * A list of {@link TopologyConnection} objects that define the possible
	 * interactions between the VALAWAI components.
	 */
	@Schema(title = "The connections between nodes in the topology.")
	public List<TopologyConnection> connections;
}
