/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorEndpoint } from "./editor-endpoint.model";


/**
 * A connection in the graph od the EditorTopology..
 */
export class EditorConnection {

	/**
	 * An optional code snippet or identifier used to transform messages from the
	 * source channel's format to the target channel's expected format. Can be null
	 * or empty if no conversion is required.
	 */
	public convertCode: string | null = null;

	/**
	 * Explain how the connection has to be painted.
	 */
	public type: string | null = null;

	/**
	 * Create a new connection to edit.
	 */
	constructor(
		public id: string,
		public source: EditorEndpoint,
		public target: EditorEndpoint,
		public isNotification: boolean = false
	) {

	}

	/**
	 * The color of the connection.
	 */
	public colorFor(selected: boolean): string {

		if (selected) {

			return 'color-red-400';

		} else {

			return 'color-sky-400';
		}
	}

	/**
	 * The color to fill the connection.
	 */
	public fillColorFor(selected: boolean): string {

		if (selected) {

			return 'fill-red-400';

		} else {

			return 'fill-sky-400';
		}
	}

	/**
	 * Check if the connection is a partial one. thus, a connection that pass thoought a notification node.
	 */
	public get isPartial(): boolean {

		return this.source.channel == null || this.target.channel == null;
	}

	/**
	 * REturn the identifier of the notification node involved in the connection if any.
	 */
	public get notificationNodeId(): string | null {

		if (this.source.channel == null) {

			return this.source.nodeId;

		} else if (this.target.channel == null) {

			return this.target.nodeId;
		} else {

			return null;
		}
	}

} 