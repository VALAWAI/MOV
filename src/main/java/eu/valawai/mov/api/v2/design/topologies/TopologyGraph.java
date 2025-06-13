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
import jakarta.validation.constraints.NotEmpty;

/**
 * A topology definition using a graph.
 *
 * @author VALAWAI
 */
@Schema(description = "The definition of a topology with a graph.")
public class TopologyGraph extends Model {

	/**
	 * The identifier of the topology.
	 */
	@Schema(description = "The identifier of the topology", readOnly = true, example = "000000000000000000000000", implementation = String.class)
	@BsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public ObjectId id;

	/**
	 * The name of the topology.
	 */
	@Schema(title = "The name of the topology.")
	@NotEmpty
	public String name;

	/**
	 * The description of the topology.
	 */
	@Schema(title = "The description of the topology.")
	public String description;

}
