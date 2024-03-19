/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


/**
 * The information necessary to create a topology connection.
 *
 * @author VALAWAI
 */
export class ConnectionToCreate {

	/**
	 * The component that is the source of the topology connection.
	 */
	public sourceComponent: string | null = null;

	/**
	 * The name of the channel that is the source of the topology connection.
	 */
	public sourceChannel: string | null = null;

	/**
	 * The component that is the target of the topology connection.
	 */
	public targetComponent: string | null = null;

	/**
	 * The name of the channel that is the target of the topology connection.
	 */
	public targetChannel: string | null = null;

    /**
	 * This is {@code true} if the connection has to be enabled.
	 */
	public enabled: boolean | null = null;


}
