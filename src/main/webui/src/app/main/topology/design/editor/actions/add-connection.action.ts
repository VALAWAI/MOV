/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { EditorConnection } from "../editor-connection.model";
import { TopologyEditorService } from "../topology.service";
import { AbstractConnectionAction } from "./abstract-connection.action";

/**
 * An actin to add a connection betwen node.
 */
export class AddConnectionAction extends AbstractConnectionAction {

	/**
	 * Create teh action to add multiple connecitons at the same time.
	 */
	constructor(
		public override connection: EditorConnection,
		private otherConnection: EditorConnection | null = null
	) {

		super(connection);
	}

	/**
	 * Remove the added connection.
	 */
	public undo(service: TopologyEditorService): void {

		var index = service.connections.findIndex(c => c.id == this.connectionId);
		service.connections.splice(index, 1);
		if (this.otherConnection != null) {

			index = service.connections.findIndex(c => c.id == this.otherConnection!.id);
			service.connections.splice(index, 1);
			service.notifyRemovedConnection(this.otherConnection!.id);
		}
		service.notifyRemovedConnection(this.connectionId);

	}

	/**
	 * Add the connection. 
	 */
	public redo(service: TopologyEditorService): void {

		service.connections.push(this.connection);
		if (this.otherConnection != null) {

			service.connections.push(this.otherConnection);
			service.notifyRemovedConnection(this.otherConnection!.id);
		}
		service.notifyAddedConnection(this.connectionId);

	}


}

