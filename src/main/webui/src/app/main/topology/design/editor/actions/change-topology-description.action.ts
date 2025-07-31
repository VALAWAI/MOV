/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyEditorService } from "../topology.service";
import { TopologyEditorAction } from '../topology.action';

/**
 * An actin to change the topology description.
 */
export class ChangeTopologyDescription implements TopologyEditorAction {

	/**
	 * The old description of the topology
	 */
	private oldDescription: string | null = null;;

	/**
	 * Create the action with the connection to be removed.
	 */
	constructor(private newDescription: string | null) {

	}

	/**
	 * Restore the previous description.
	 */
	public undo(service: TopologyEditorService): void {

		service.min.description = this.oldDescription;
		service.notifyChangedTopology();
	}

	/**
	 * Set the new topology description. 
	 */
	public redo(service: TopologyEditorService): void {

		this.oldDescription = service.min.description;
		service.min.description = this.newDescription;
		service.notifyChangedTopology();

	}

}

