/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import jakarta.validation.constraints.NotEmpty;

/**
 * Represents a single endpoint of a connection, comprising a node's unique tag
 * and the specific channel name on that node.
 *
 * @see TopologyConnection
 *
 * @author VALAWAI
 */
@Schema(description = "Represents a specific endpoint of a connection: a node's tag and its channel.")
public class TopologyConnectionEndpoint extends Model {

	/**
	 * The unique identifier (tag) of the node involved in this endpoint.
	 */
	@Schema(description = "The unique identifier (tag) of the node.")
	@NotEmpty
	public String nodeTag;

	/**
	 * The name of the channel on the node involved in this endpoint. For a source
	 * endpoint, this is an output channel. For a target endpoint, this is an input
	 * channel.
	 */
	@Schema(description = "The name of the channel on the node (output for source, input for target).")
	@NotEmpty
	public String channel;

}
