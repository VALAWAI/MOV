/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Represents a logical topology definition of the components that define the
 * interaction between the VALAWAI component.
 *
 * @see TopologyNode
 * @see TopologyConnection
 *
 * @author VALAWAI
 */
@Schema(description = "The definition of a topology with a graph.")
public class Topology extends MinTopology {

	/**
	 * A list of {@link TopologyNode} objects that represent the different VALAWAI
	 * components that form the value aware application.
	 */
	@Schema(title = "The nodes defined within the topology.")
	public List<TopologyNode> nodes;

	/**
	 * A list of {@link TopologyConnection} objects that define the possible
	 * interactions between the VALAWAI components.
	 */
	@Schema(title = "The connections between nodes in the topology.")
	public List<TopologyConnection> connections;
}
