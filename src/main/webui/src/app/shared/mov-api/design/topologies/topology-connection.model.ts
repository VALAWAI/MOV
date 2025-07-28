/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyConnectionNotification } from './iopology-connection-notification.model';
import { Point } from './point.model';
import { TopologyConnectionEndpoint } from './topology-connection-endpoint.model';

export type TopologyGraphConnectionType = 'STRAIGHT' | 'BEZIER' | 'SEGMENT';

export function toTopologyGraphConnectionType(value: string | undefined | null): TopologyGraphConnectionType | null {

	if (value != null) {

		var type = value.toUpperCase().trim();
		if (type == 'STRAIGHT' || type == 'BEZIER' || type == 'SEGMENT') {

			return type;
		}
	}

	return null;


}

/**
 * Represents a directed connection between two nodes within a {@link Topology}.
 * This connection specifies the exact output channel on the source node and the
 * exact input channel on the target node involved in the data flow.
 *
 * @author VALAWAI
 */
export class TopologyConnection {

	/**
	 * The source of the connection, specifying the originating node's tag and the
	 * specific output channel from which data flows.
	 */
	public source: TopologyConnectionEndpoint | null = null;

	/**
	 * The target of the connection, specifying the destination node's tag and the
	 * specific input channel where data is received.
	 */
	public target: TopologyConnectionEndpoint | null = null;

	/**
	 * An optional code snippet or identifier used to transform messages from the
	 * source channel's format to the target channel's expected format. Can be null
	 * or empty if no conversion is required.
	 */
	public convertCode: string | null = null;

	/**
	 * Explain how the connection has to be painted.
	 */
	public type: TopologyGraphConnectionType | null = null;

	/**
	 * The list of nodes that will be notified when a message pass through this
	 * connection.
	 */
	public notifications: TopologyConnectionNotification[] | null = null;

	/**
	 * The position of the notification node.
	 */
	public notificationPosition: Point | null = null;


}
