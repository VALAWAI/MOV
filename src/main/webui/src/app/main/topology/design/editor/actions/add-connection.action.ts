/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorConnection } from "../editor-connection.model";
import { EditorTopologyService, TopologyChangeAction } from "../editor-topology.service";

/**
 * An actin to add a connection betwen node.
 */
export class AddConnectionAction extends TopologyChangeAction {


	/**
	 * Create the action with the connection to be removed.
	 */
	constructor(public connection: EditorConnection) {

		super();
	}

	/**
	 * Undo teh action. Thus remove the added connection.
	 */
	public override undo(service: EditorTopologyService): void {

		var index = service.connections.findIndex(c => c.id == this.connection.id);
		this.connection = service.connections.splice(index, 1)[0];

	}

	/**
	 * Remove the connection. 
	 */
	public override redo(service: EditorTopologyService): void {

		service.connections.push(this.connection);

	}


}

