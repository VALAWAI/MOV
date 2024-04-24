/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyAction } from "./topology-action.model";


/**
 * The changes to do over a topology connection.
 *
 * @author VALAWAI
 */
export class ChangeConnection {

	/**
	 * The type of action to do on the topology.
	 */
	public action: TopologyAction | null = null;

	/**
	 * The identifier of the topology connection to change.
	 */
	public connectionId: string | null = null;

}
