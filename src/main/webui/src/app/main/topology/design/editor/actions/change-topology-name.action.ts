/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorTopologyService, TopologyChangeAction } from "../editor-topology.service";

/**
 * An actin to change the topology name.
 */
export class ChangeTopologyName extends TopologyChangeAction {

	/**
	 * The old name of the topology
	 */
	private oldName: string | null = null;;

	/**
	 * Create the action with the connection to be removed.
	 */
	constructor(private newName: string | null) {

		super();
	}

	/**
	 * Restore the previous name.
	 */
	public override undo(service: EditorTopologyService): void {

		service.min.name = this.oldName;
	}

	/**
	 * Set the new topology name. 
	 */
	public override redo(service: EditorTopologyService): void {

		this.oldName = service.min.name;
		service.min.name = this.newName;

	}

}

