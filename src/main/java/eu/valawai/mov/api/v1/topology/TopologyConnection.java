/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.Model;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import io.smallrye.common.constraint.NotNull;

/**
 * The information of a topology connection.
 *
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
@Schema(description = "The information of a topology connection.")
public class TopologyConnection extends Model {

	/**
	 * The identifier of the topology connection.
	 */
	@Schema(description = "The identifier of the topology connection", readOnly = true, example = "000000000000000000000000", implementation = String.class)
	@BsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;

	/**
	 * This is true if the connection is enabled.
	 */
	@Schema(description = "This is true if the connection is enabled.")
	@NotNull
	public boolean enabled;

	/**
	 * The epoch time, in seconds, when the connection has been created.
	 */
	@Schema(description = "The epoch time, in seconds, when the connection has been created.")
	@NotNull
	public long createTimestamp;

	/**
	 * The epoch time, in seconds, when the connection has been updated.
	 */
	@Schema(description = "The epoch time, in seconds, when the connection has been updated.")
	@NotNull
	public long updateTimestamp;

	/**
	 * The source of the connection.
	 */
	@Schema(description = "The source of the connection.")
	@NotNull
	public TopologyConnectionNode source;

	/**
	 * The target of the connection.
	 */
	@Schema(description = "The target of the connection.")
	@NotNull
	public TopologyConnectionNode target;

	/**
	 * The components to notify when a message pass thought this connection
	 */
	@Schema(description = "The components to notify when a message pass thought this connection.")
	public List<TopologyConnectionNode> subscriptions;

}
