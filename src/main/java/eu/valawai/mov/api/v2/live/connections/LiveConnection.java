/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.connections;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import io.smallrye.common.constraint.NotNull;

/**
 * A connection that is active in the topology.
 *
 * @author VALAWAI
 */
@Schema(title = "A connection that is active in the topology.")
public class LiveConnection {

	/**
	 * The identifier of the live topology connection.
	 */
	@Schema(description = "The identifier of the live connection", readOnly = true, examples = "000000000000000000000000", implementation = String.class)
	@BsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;

	/**
	 * This is true if the connection is enabled.
	 */
	@Schema(description = "This is true if the connection is enabled.")
	@NotNull
	public boolean enabled;

}
