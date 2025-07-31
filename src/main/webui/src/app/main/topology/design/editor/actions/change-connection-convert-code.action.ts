/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { TopologyEditorService } from "../topology.service";
import { ChangeConnectionAction } from "./change-connection.action";

/**
 * An actin taht change the convert code of a connection.
 */
export class ChangeConnectionConvertCodeAction implements ChangeConnectionAction {

	/**
	 * The previous conneciton target.
	 */
	private oldConvertCode: string | null = null;

	/**
	 * Create the action with the connection to be removed.
	 */
	constructor(public connectionId: string, private newConvertCode: string | null) {

	}

	/**
	 * REtore the old target.
	 */
	public undo(service: TopologyEditorService): void {

		const connection = service.getConnectionWith(this.connectionId)!;
		connection.convertCode = this.oldConvertCode;
		service.notifyChangedConnection(this.connectionId);

	}

	/**
	 * Change the connection target. the connection. 
	 */
	public redo(service: TopologyEditorService): void {

		const connection = service.getConnectionWith(this.connectionId)!;
		this.oldConvertCode = connection.convertCode;
		connection.convertCode = this.newConvertCode;
		service.notifyChangedConnection(this.connectionId);
	}


}

