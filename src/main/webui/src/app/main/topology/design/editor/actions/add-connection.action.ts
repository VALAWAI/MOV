/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { TopologyEditorService } from "../topology.service";
import { AbstractConnectionAction } from "./abstract-connection.action";

/**
 * An actin to add a connection betwen node.
 */
export class AddConnectionAction extends AbstractConnectionAction {


	/**
	 * Remove the added connection.
	 */
	public undo(service: TopologyEditorService): void {

		var index = service.connections.findIndex(c => c.id == this.connection.id);
		service.connections.splice(index, 1);
		service.notifyRemovedConnection(this.connectionId);

	}

	/**
	 * Add the connection. 
	 */
	public redo(service: TopologyEditorService): void {

		service.connections.push(this.connection);
		service.notifyAddedConnection(this.connectionId);

	}


}

