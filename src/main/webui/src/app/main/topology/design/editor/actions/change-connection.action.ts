/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyChangeAction } from "../editor-topology.service";

/**
 * An actin that change a connection.
 */
export abstract class ChangeConnectionAction extends TopologyChangeAction {

	/**
	 * The identifier of the connection that has changed. 
	 */
	public abstract get connectionId(): string;

	
	
}

