/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


/**
 * An endpoint in with a {@link LiveTopologyComponentOutConnection} can be
 * connected.
 *
 * @author VALAWAI
 */
export class LiveTopologyConnectionEndpoint {

	/**
	 * The identifier of the component.
	 */
	public id: string | null = null;

	/**
	 * The name of the channel on the node involved in this endpoint. Thus the
	 * channel in the node that the connection will end.
	 */
	public channel: string | null = null;

	/**
	 * This is {@code true} if the connection is enabled.
	 */
	public enabled: boolean = false;
}