/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorConnection } from "../editor-connection.model";
import { EditorTopologyService } from "../editor-topology.service";
import { ChangeConnectionAction } from "./change-connection.action";

/**
 * An actin to remove a connection.
 */
export class RemoveConnectionAction extends ChangeConnectionAction {

	/**
	 * The conneciton tha has been removed.
	 */
	private connection: EditorConnection | null = null;

	/**
	 * Create the action with the connection to be removed.
	 */
	constructor(public override connectionId: string) {

		super();
	}

	/**
	 * Undo the remove.
	 */
	public override undo(service: EditorTopologyService): void {

		service.connections.push(this.connection!);

	}

	/**
	 * Remove the connection. 
	 */
	public override redo(service: EditorTopologyService): void {

		var index = service.connections.findIndex(c => c.id == this.connectionId);
		this.connection = service.connections.splice(index, 1)[0];

	}


}

