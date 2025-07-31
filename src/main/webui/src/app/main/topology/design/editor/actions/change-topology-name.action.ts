/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyEditorAction } from "../topology.action";
import { TopologyEditorService } from "../topology.service";

/**
 * An actin to change the topology name.
 */
export class ChangeTopologyName implements TopologyEditorAction {

	/**
	 * The old name of the topology
	 */
	private oldName: string | null = null;;

	/**
	 * Create the action with the connection to be removed.
	 */
	constructor(private newName: string | null) {

	}

	/**
	 * Restore the previous name.
	 */
	public undo(service: TopologyEditorService): void {

		service.min.name = this.oldName;
		service.notifyChangedTopology();
	}

	/**
	 * Set the new topology name. 
	 */
	public redo(service: TopologyEditorService): void {

		this.oldName = service.min.name;
		service.min.name = this.newName;
		service.notifyChangedTopology();
	}

}

