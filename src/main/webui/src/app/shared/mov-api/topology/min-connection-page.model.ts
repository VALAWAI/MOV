/*
  Copyright 2022-2026 VALAWAY

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

}
