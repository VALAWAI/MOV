/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.topologies;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.Model;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;

/**
 * An endpoint in with a {@link LiveTopologyComponentOutConnection} can be
 * connected.
 *
 * @see LiveTopologyComponentOutConnection
 *
 * @author VALAWAI
 */
@Schema(title = "An endpoint in with a connection can be connected.")
public class LiveTopologyConnectionEndpoint extends Model {

	/**
	 * The identifier of the component.
	 */
	@Schema(description = "The identifier of the node component", readOnly = true, examples = "000000000000000000000000", implementation = String.class)
	@BsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;

	/**
	 * The name of the channel on the node involved in this endpoint. Thus the
	 * channel in the node that the connection will end.
	 */
	@Schema(description = "The name of the channel on the node.")
	public String channel;

	/**
	 * This is {@code true} if the connection is enabled.
	 */
	@Schema(description = "This is true if the connection is enabled.")
	public boolean enabled;

}
