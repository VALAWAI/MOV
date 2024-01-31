/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonRootName;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * The information necessary to change the topology.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@RegisterForReflection
@JsonRootName("change_topology_payload")
@Schema(title = "The information necessary to change the topology.")
public class ChangeTopologyComponentPayload extends Payload {

	/**
	 * he type of action to do on the topology.
	 */
	@Schema(title = "The type of action to do on the topology.")
	public TopologyAction action;

	/**
	 * The source channel of the topology to modify.
	 */
	@Schema(title = "The source channel of the topology to modify.")
	public String source;

	/**
	 * The target channel of the topology to modify.
	 */
	@Schema(title = "The target channel of the topology to modify.")
	public String target;
}
