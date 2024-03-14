/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.Model;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotEmpty;

/**
 * The minimal information of a connection.
 *
 * @author VALAWAI
 */
public class MinConnection extends Model {

	/**
	 * The identifier of the connection.
	 */
	@Schema(title = "The identifier of the connection", readOnly = true, example = "000000000000000000000000", implementation = String.class)
	@BsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;

	/**
	 * The source of the channel name of the connection.
	 */
	@Schema(title = "The source channel name of the connection.")
	@NotEmpty
	public String source;

	/**
	 * The target of the channel name of the connection.
	 */
	@Schema(title = "The target channel name of the connection.")
	@NotEmpty
	public String target;

	/**
	 * The type of connection.
	 */
	@Schema(title = "The connection type.")
	@NotNull
	public boolean enabled;

}
