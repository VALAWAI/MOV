/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import eu.valawai.mov.persistence.design.topology.TopologyGraphConnectionType;

/**
 * Represents a directed connection between two nodes within a {@link Topology}.
 * This connection specifies the exact output channel on the source node and the
 * exact input channel on the target node involved in the data flow.
 *
 * @see Topology
 * @see TopologyNode
 *
 * @author VALAWAI
 */
@Schema(title = "Connection between two nodes of a topology.", description = "Represents a directed connection between two nodes in a topology, specifying the source and target channels.")
public class TopologyConnection extends Model {

	/**
	 * The source of the connection, specifying the originating node's tag and the
	 * specific output channel from which data flows.
	 */
	@Schema(description = "The source of the connection, including the source node's tag and output channel.")
	public TopologyConnectionEndpoint source;

	/**
	 * The target of the connection, specifying the destination node's tag and the
	 * specific input channel where data is received.
	 */
	@Schema(description = "The target of the connection, including the target node's tag and input channel.")
	public TopologyConnectionEndpoint target;

	/**
	 * An optional code snippet or identifier used to transform messages from the
	 * source channel's format to the target channel's expected format. Can be null
	 * or empty if no conversion is required.
	 */
	@Schema(description = "An optional code or identifier for message transformation between source and target channels.")
	public String convertCode;

	/**
	 * Explain how the connection has to be painted.
	 */
	@Schema(description = "Explain how the connection has to be painted.")
	public TopologyGraphConnectionType type;

	/**
	 * The list of nodes that will be notified when a message pass through this
	 * connection.
	 */
	@Schema(description = "The list of nodes that will be notified when a message pass through this connection.")
	public List<TopologyConnectionNotification> notifications;

	/**
	 * The position of the notification node.
	 */
	@Schema(description = "The position of the notification node.")
	public Point notificationPosition;
}
