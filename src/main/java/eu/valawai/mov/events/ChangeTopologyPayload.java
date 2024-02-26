/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

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
public class ChangeTopologyComponentPayload extends Payload {

	/**
	 * he type of action to do on the topology.
	 */
	@Schema(title = "The type of action to do on the topology.")
	@NotNull
	public TopologyAction action;

	/**
	 * The source channel of the topology to modify.
	 */
	@Schema(title = "The source channel of the topology to modify.")
	@NotNull
	public String source;

	/**
	 * The target channel of the topology to modify.
	 */
	@Schema(title = "The target channel of the topology to modify.")
	@NotNull
	public String target;
}
