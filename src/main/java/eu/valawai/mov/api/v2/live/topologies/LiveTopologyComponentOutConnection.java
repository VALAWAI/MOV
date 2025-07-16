/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.topologies;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.Model;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;

/**
 * A connection from a node to another one.
 *
 * @see LiveTopologyComponent
 *
 * @author VALAWAI
 */
@Schema(title = "The connection from anode to another one.")
public class LiveTopologyComponentOutConnection extends Model {

	/**
	 * The identifier of the connection.
	 */
	@Schema(description = "The identifier of the connection", readOnly = true, examples = "000000000000000000000000", implementation = String.class)
	@BsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;

	/**
	 * The name of the channel on the source node.
	 */
	@Schema(description = "The name of the channel on the source node.")
	public String channel;

	/**
	 * The node that the connection go.
	 */
	@Schema(description = "The tag of the target node.")
	public LiveTopologyConnectionEndpoint target;

	/**
	 * The list of nodes that will notify when a message pass thought this
	 * connection.
	 */
	@Schema(title = "The list of nodes that will notify when a message pass through this connection.")
	public List<LiveTopologyConnectionEndpoint> notifications;

}
