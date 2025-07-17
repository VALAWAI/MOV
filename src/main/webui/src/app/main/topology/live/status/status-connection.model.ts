/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { StatusEndpoint } from "./status-endpoint.model";


/**
 * A connection between status nodes.
 */
export class StatusConnection {

	/**
	 * This is {@code true} if the connection is enabled.
	 */
	public enabled: boolean = true;

	/**
	 * Create the connection.
	 */
	constructor(public source: StatusEndpoint, public target: StatusEndpoint) {

	}

	/**
	 * Return the id of the connection.
	 */
	public get id(): string {

		return this.source.id + "->" + this.target.id;
	}


	/**
	 * Return the color for the connextion.
	 */
	public color(selected: boolean): string {

		if (selected) {

			return 'var(--color-red-800)';

		} else if (this.enabled) {

			return 'var(--color-sky-400)';

		} else {

			return 'var(--color-gray-200)';
		}

	}


}
