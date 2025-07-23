/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { DesignTopologyConnection } from "@app/shared/mov-api";
import { EditorEndpoint } from "./editor-endpoint.model";




/**
 * A connection in the graph od the EditorTopology..
 */
export class EditorConnection {

	/**
	 * The identifeir of the connection.
	 */
	public id: string = 'connection_0';


	/**
	 * Create the connection.
	 */
	constructor(
		public model: DesignTopologyConnection,
		public source: EditorEndpoint,
		public target: EditorEndpoint,
	) {

	}
	

	/**
	 * The type of the connection.
	 */
	public get type(): string | null {

		if ('type' in this.model) {

			return this.model.type;
		}

		return null;

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