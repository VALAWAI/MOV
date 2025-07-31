/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyEditorService } from "./topology.service";


/**
 * An action that chnage the topology.
 */
export interface TopologyEditorAction {

	/**
	 * Undo the topology change action.
	 */
	undo(service: TopologyEditorService): void;

	/**
	 * Redo the topology change action.
	 */
	 redo(service: TopologyEditorService): void;
}
