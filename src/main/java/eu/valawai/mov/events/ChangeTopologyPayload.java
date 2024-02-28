/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonRootName;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;

/**
 * The information necessary to change the topology.
 *
 * @author VALAWAI
 */
@RegisterForReflection
@JsonRootName("change_topology_payload")
@Schema(title = "The information necessary to change the topology.")
public class ChangeTopologyPayload extends Payload {

	/**
	 * he type of action to do on the topology.
	 */
	@Schema(title = "The type of action to do on the topology.")
	@NotNull
	public TopologyAction action;

	/**
	 * The identifier of the connection to modify.
	 */
	@Schema(title = "The identifier of the connection to modify.")
	@NotNull
	public ObjectId connectionId;

}
