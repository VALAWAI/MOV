/*
  Copyright 2022-2026 VALAWAI

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

	/**
	 * Check if this connection is equals to another.
	 */
	public static equals(source: MinConnection | null | undefined, target: MinConnection | null | undefined): boolean {

		return source != null
			&& target != null
			&& source.id === target.id
			&& source.source === target.source
			&& source.target === target.target
			&& source.enabled === target.enabled;
	}


}
