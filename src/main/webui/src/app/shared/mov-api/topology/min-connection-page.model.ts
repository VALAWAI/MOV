/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { MinConnection } from "./min-connection.model";

/**
 * A page with some connections.
 *
 * @author VALAWAI
 */
export class MinConnectionPage {

	/**
	 * The number of connections that satisfy the query.
	 */
	public total: number = 0;

	/**
	 * The offset of the first returned connection.
	 */
	public offset: number = 0;

	/**
	 * The connections that match the query.
	 */
	public connections: MinConnection[] | null = null;

	/**
	 * Check if this page is equals to another.
	 */
	public static equals(source: MinConnectionPage | null | undefined, target: MinConnectionPage | null | undefined): boolean {

		if (source == null && target == null) {

			return true;

		} else if (
			source != null
			&& target != null
			&& source.total === target.total
		) {
			if ((source.connections == null || source.connections.length == 0)
				&& (target.connections == null || target.connections.length == 0)
			) {
				
				return true;

			} else if (
				source.connections != null
				&& target.connections != null
				&& source.connections.length === target.connections.length
			) {

				for (var i = 0; i < source.connections.length; i++) {

					var sourceComponent = source.connections[i];
					var targetComponent = target.connections[i];
					if (!MinConnection.equals(sourceComponent, targetComponent)) {

						return false;
					}
				}
				return true;
			}
		}

		return false;

	}

}
