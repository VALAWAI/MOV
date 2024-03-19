/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.Model;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * The information necessary to create a topology connection.
 *
 * @author VALAWAI
 */
@Schema(title = "The information necessary to create a topology connection.")
public class ConnectionToCreate extends Model {

	/**
	 * The component that is the source of the topology connection.
	 */
	@Schema(title = "The component that is the source of the topology connection.")
	@JsonSerialize(using = ObjectIdSerializer.class)
	@NotNull
	public ObjectId sourceComponent;

	/**
	 * The name of the channel that is the source of the topology connection.
	 */
	@Schema(title = "The name of the channel that is the source of the topology connection.")
	@NotEmpty
	public String sourceChannel;

	/**
	 * The component that is the target of the topology connection.
	 */
	@Schema(title = "The component that is the target of the topology connection.")
	@JsonSerialize(using = ObjectIdSerializer.class)
	@NotNull
	public ObjectId targetComponent;

	/**
	 * The name of the channel that is the target of the topology connection.
	 */
	@Schema(title = "The name of the channel that is the target of the topology connection.")
	@NotEmpty
	public String targetChannel;

	/**
	 * This is {@code true} if the connection has to be enabled.
	 */
	@Schema(title = "When it is true, the connection will be started after it has been created.")
	@NotNull
	public boolean enabled;

}
