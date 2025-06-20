/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import eu.valawai.mov.api.v2.design.components.ComponentDefinition;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a single node within a {@link Topology} graph, defining its unique
 * identity, its graphical position, and the {@link ComponentDefinition} that it
 * embodies.
 *
 * @see Topology
 *
 * @author VALAWAI
 */
@Schema(title = "A node in the topology graph.")
public class TopologyNode extends Model {

	/**
	 * The unique identifier or tag for this node within the topology. This ID helps
	 * in establishing connections ({@link TopologyConnection}) to and from this
	 * specific node.
	 */
	@Schema(description = "The unique identifier (tag) of the node within the topology. Used for connecting nodes.", examples = "my-processor-node-1")
	@NotEmpty(message = "Node ID (tag) cannot be empty.")
	public String tag;

	/**
	 * The graphical coordinates (X and Y) of the node within the topology
	 * visualization. This determines where the node is displayed on a canvas or
	 * diagram.
	 */
	@Schema(description = "The graphical X,Y position of the node within the topology visualization.")
	@NotNull(message = "Node position cannot be null.")
	public Point position;

	/**
	 * A reference to the {@link ComponentDefinition} that this node represents.
	 * This defines the functional behavior and capabilities of the node.
	 */
	@Schema(description = "The component definition that this node embodies, defining its functionality.")
	public ComponentDefinition component;
}
