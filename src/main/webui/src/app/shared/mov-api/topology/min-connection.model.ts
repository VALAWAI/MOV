/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


/**
 * A connection that is in VALAWAI.
 *
 * @author VALAWAI
 */
export class MinConnection {

	/**
	 * The identifier of the connection.
	 */
	public id: string | null = null;

	/**
	 * The source channel name of the connection.
	 */
	public source: string | null = null;

	/**
	 * The target channel name of the connection.
	 */
	public target: string | null = null;

    /**
	 * This is true if the connection is enabled.
	 */
	public enabled: boolean | null = null;


}
