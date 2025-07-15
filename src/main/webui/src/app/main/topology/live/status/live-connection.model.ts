/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { MinConnection } from '@app/shared/mov-api';

/**
 * A connection in the live topology.
 */
export class LiveConnection {

	/**
	 * The identifier of the source node of teh connection.
	 */
	public sourceNodeId: string = 'node_0';

	/**
	 * The identifier of the target node of teh connection.
	 */
	public targetNodeId: string = 'node_0';

	/**
	 * Create the connection.
	 */
	constructor(
		public connection: MinConnection
	) {

	}

	/**
	 * The idetifier of the source.
	 */
	get sourceId(): string {

		return this.connection.source || '';
	}

	/**
	 * The idetifier of the target.
	 */
	get targetId(): string {

		return this.connection.target || '';
	}

	/**
	 * The identifier of the connection.
	 */
	public get id(): string {

		return this.connection.id || 'connection_0';
	}

	/**
	 * Check if teh connection is enaled.
	 */
	get isEnabled(): boolean {

		return this.connection.enabled == true || false;
	}


}
