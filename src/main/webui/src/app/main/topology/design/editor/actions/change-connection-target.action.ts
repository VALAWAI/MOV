/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { EditorEndpoint } from "../editor-endpoint.model";
import { TopologyEditorService } from "../topology.service";
import { ChangeConnectionAction } from "./change-connection.action";

/**
 * An actin taht change the target of a connection.
 */
export class ChangeConnectionTargetAction implements ChangeConnectionAction {

	/**
	 * The previous conneciton target.
	 */
	private oldTarget: EditorEndpoint | null = null;

	/**
	 * Create the action with the connection to be removed.
	 */
	constructor(public connectionId: string, public newTargetEndpoint: EditorEndpoint) {

	}

	/**
	 * REtore the old target.
	 */
	public undo(service: TopologyEditorService): void {

		const connection = service.getConnectionWith(this.connectionId)!;
		connection!.target = this.oldTarget!;
		service.notifyChangedConnection(this.connectionId);

	}

	/**
	 * Change the connection target. the connection. 
	 */
	public redo(service: TopologyEditorService): void {

		const connection = service.getConnectionWith(this.connectionId)!;
		this.oldTarget = connection.target;
		var targetNode = service.getNodeWith(this.newTargetEndpoint.nodeId)!;
		connection.target = targetNode.searchEndpointOrCreate(this.newTargetEndpoint.channel, false);
		service.notifyChangedConnection(this.connectionId);
	}


}

