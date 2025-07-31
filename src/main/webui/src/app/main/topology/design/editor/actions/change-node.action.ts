/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyEditorAction } from "../topology.action";

/**
 * An actin that change a node.
 */
export interface ChangeNodeAction extends TopologyEditorAction {

	/**
	 * The identifier of the node that has changed. 
	 */
	nodeId: string;

}

