/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.topology;

import java.util.List;

import org.bson.types.ObjectId;

import eu.valawai.mov.api.Model;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntity;

/**
 * Represents a node within a {@link TopologyGraphEntity}, defining its unique
 * identity, visual position, associated component, and outgoing connections.
 * Nodes are fundamental building blocks of a topology, representing distinct
 * functional units or entities.
 *
 * @see TopologyGraphEntity
 *
 * @author VALAWAI
 */
public class TopologyGraphNode extends Model {

	/**
	 * The unique identifier or tag for this node. This allows for unambiguous
	 * referencing of the node within the topology graph.
	 */
	public String tag;

	/**
	 * The X-coordinate of the node's position within the graphical representation
	 * of the topology. This typically represents its horizontal placement.
	 */
	public double x;

	/**
	 * The Y-coordinate of the node's position within the graphical representation
	 * of the topology. This typically represents its vertical placement.
	 */
	public double y;

	/**
	 * A reference to the {@link ComponentDefinitionEntity} that defines the
	 * functional characteristics and properties of this node. This links the
	 * graphical node to its underlying component definition.
	 */
	public ObjectId componentRef;

	/**
	 * A list of {@link TopologyGraphNodeOutputConnection} objects, each
	 * representing a directed connection originating from an output channel of this
	 * node to another node in the topology. This defines the data flow leaving this
	 * node.
	 */
	public List<TopologyGraphNodeOutputConnection> outputs;
}
