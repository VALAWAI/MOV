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
import eu.valawai.mov.events.topology.TopologyAction;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import jakarta.validation.constraints.NotNull;

/**
 * The changes to do over a topology connection.
 *
 * @author VALAWAI
 */
@Schema(title = "The information necessary to change a topology connection.")
public class ChangeConnection extends Model {

	/**
	 * he type of action to do on the topology.
	 */
	@Schema(title = "The type of action to do over a topology connection.")
	@NotNull
	public TopologyAction action;

	/**
	 * The identifier of the topology connection to change.
	 */
	@Schema(title = "The identifier of the topology connection to change.")
	@NotNull
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId connectionId;

}
