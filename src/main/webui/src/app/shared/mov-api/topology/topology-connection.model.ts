/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyConnectionNode } from './topology-connection-node.model';

/**
 * A topology connection that is in VALAWAI.
 *
 * @author VALAWAI
 */
export class TopologyConnection {

	/**
	 * The identifier of the topology connection.
	 */
	public id: string | null = null;

	/**
	 * This is true if the connection is enabled.
	 */
	public enabled: boolean | null = null;

	/**
	 * The epoch time, in seconds, when the connection has been created.
	 */
	public createTimestamp: number | null = null;

	/**
	 * The epoch time, in seconds, when the connection has been updated.
	 */
	public updateTimestamp: number | null = null;

	/**
	 * The source of the connection.
	 */
	public source: TopologyConnectionNode | null = null;

	/**
	 * The target of the connection.
	 */
	public target: TopologyConnectionNode | null = null;

	/**
	 * The components that are subscribed to receive shat happens on this connection.
	 */
	public subscriptions: TopologyConnectionNode[] | null = null;

}
