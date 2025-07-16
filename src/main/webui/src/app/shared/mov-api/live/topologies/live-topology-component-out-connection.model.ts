/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { LiveTopologyConnectionEndpoint } from './live-topology-connection-endpoint.model';


/**
 *A connection from a node to another one.
 *
 * @author VALAWAI
 */
export class LiveTopologyComponentOutConnection {

	/**
	 * The identifier of the connection.
	 */
	public id: string | null = null;

	/**
	 * The name of the channel on the source node.
	 */
	public channel: string | null = null;

	/**
	 * The node that the connection go.
	 */
	public target: LiveTopologyConnectionEndpoint | null = null;

	/**
	 * The list of nodes that will notify when a message pass thought this
	 * connection.
	 */
	public notifications: LiveTopologyConnectionEndpoint[] | null = null;

}
