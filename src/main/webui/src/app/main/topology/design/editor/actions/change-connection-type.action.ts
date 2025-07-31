/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { TopologyEditorService } from "../topology.service";
import { ChangeConnectionAction } from "./change-connection.action";

/**
 * An actin taht change the type of a connection.
 */
export class ChangeConnectionTypeAction implements ChangeConnectionAction {

	/**
	 * The previous conneciton target.
	 */
	private oldType: string | null = null;

	/**
	 * Create the action with the connection to be removed.
	 */
	constructor(public connectionId: string, private newType: string | null) {

	}

	/**
	 * REtore the old target.
	 */
	public undo(service: TopologyEditorService): void {

		const connection = service.getConnectionWith(this.connectionId)!;
		connection.type = this.oldType;
		service.notifyChangedConnection(this.connectionId);

	}

	/**
	 * Change the connection target. the connection. 
	 */
	public redo(service: TopologyEditorService): void {

		const connection = service.getConnectionWith(this.connectionId)!;
		this.oldType = connection.type;
		connection.type = this.newType;
		service.notifyChangedConnection(this.connectionId);
	}


}

