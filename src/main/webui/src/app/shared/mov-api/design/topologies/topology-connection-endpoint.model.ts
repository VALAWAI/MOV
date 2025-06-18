/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


/**
 * Represents a single endpoint of a connection, comprising a node's unique tag
 * and the specific channel name on that node.
 *
 * @author VALAWAI
 */
export class TopologyConnectionEndpoint {

	/**
	 * The unique identifier (tag) of the node involved in this endpoint.
	 */
	public nodeTag: string | null = null;

	/**
	 * The name of the channel on the node involved in this endpoint. For a source
	 * endpoint, this is an output channel. For a target endpoint, this is an input
	 * channel.
	 */
	public channel: string | null = null;

}
