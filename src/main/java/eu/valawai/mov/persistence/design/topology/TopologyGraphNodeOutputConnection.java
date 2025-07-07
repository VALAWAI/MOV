/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.topology;

import eu.valawai.mov.api.Model;

/**
 * Represents a directed connection from an output channel of one node to an
 * input channel of another node within a topology graph. This class defines the
 * properties of such a connection, including the source and target channels,
 * the identifier of the target node, and an optional conversion code for
 * messages.
 *
 * @see TopologyGraphEntity
 * @see TopologyGraphNode
 *
 * @author VALAWAI
 */
public class TopologyGraphNodeOutputConnection extends Model {

	/**
	 * The name of the output channel on the source node from which messages
	 * originate. This channel defines the type and flow of data leaving the source
	 * node.
	 */
	public String sourceChannel;

	/**
	 * The unique identifier (tag) of the target node to which this connection is
	 * directed. This ensures messages are routed to the correct destination node
	 * within the graph.
	 */
	public String targetTag;

	/**
	 * The name of the input channel on the target node where messages are to be
	 * delivered. This channel defines how the target node receives and processes
	 * incoming data.
	 */
	public String targetChannel;

	/**
	 * An optional code snippet or reference used to transform messages from the
	 * format of the {@code sourceChannel} to the expected format of the
	 * {@code targetChannel}. If no conversion is needed, this field may be
	 * {@code null} or empty.
	 */
	public String convertCode;

	/**
	 * The visual type of the connection. Thus how the connection is represented in
	 * the UI.
	 */
	public TopologyGraphConnectionType type;

}
