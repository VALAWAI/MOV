/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorTopologyService, TopologyChangeAction } from "../editor-topology.service";

/**
 * An actin to change the topology description.
 */
export class ChangeTopologyDescription extends TopologyChangeAction {

	/**
	 * The old description of the topology
	 */
	private oldDescription: string | null = null;;

	/**
	 * Create the action with the connection to be removed.
	 */
	constructor(private newDescription: string | null) {

		super();
	}

	/**
	 * Restore the previous description.
	 */
	public override undo(service: EditorTopologyService): void {

		service.min.description = this.oldDescription;
	}

	/**
	 * Set the new topology description. 
	 */
	public override redo(service: EditorTopologyService): void {

		this.oldDescription = service.min.description;
		service.min.description = this.newDescription;

	}

}

