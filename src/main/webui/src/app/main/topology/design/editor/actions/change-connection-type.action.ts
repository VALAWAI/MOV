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
	constructor(public connectionId: string, private newType: string | null, private otherId: string) {

	}

	/**
	 * REtore the old target.
	 */
	public undo(service: TopologyEditorService): void {

		this.toogleType(this.oldType, service);

	}

	/**
	 * Change the connection target. the connection. 
	 */
	public redo(service: TopologyEditorService): void {

		this.oldType = this.toogleType(this.newType, service);
	}

	/**
	 * Toogle the type.
	 */
	private toogleType(type: string | null, service: TopologyEditorService): string | null {


		const connection = service.getConnectionWith(this.connectionId)!;
		var previous = connection.type;
		connection.type = type;
		if (this.otherId != this.connectionId) {

			const otherConnection = service.getConnectionWith(this.otherId)!;
			otherConnection.type = type;
			service.notifyChangedConnection(this.otherId);
		}
		service.notifyChangedConnection(this.connectionId);
		return previous;

	}


}

