/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyGraphConnectionType } from "@app/shared/mov-api";
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
		public sourceNotification: EditorEndpoint | null = null
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

} 