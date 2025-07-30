/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorConnection } from "../editor-connection.model";
import { EditorEndpoint } from "../editor-endpoint.model";
import { EditorTopologyService } from "../editor-topology.service";
import { ChangeConnectionAction } from "./change-connection.action";

/**
 * An actin taht change teh target of a connection.
 */
export class ChangeConnectionTargetAction extends ChangeConnectionAction {

	/**
	 * The conneciton tha has been chnaged.
	 */
	private connection: EditorConnection | null = null;

	/**
	 * The previous conneciton target.
	 */
	private oldTarget: EditorEndpoint | null = null;

	/**
	 * Create the action with the connection to be removed.
	 */
	constructor(public override connectionId: string, public newTargetEndpoint: EditorEndpoint) {

		super();
	}

	/**
	 * Undo the remove.
	 */
	public override undo(service: EditorTopologyService): void {

		this.connection!.target = this.oldTarget!;

	}

	/**
	 * Remove the connection. 
	 */
	public override redo(service: EditorTopologyService): void {

		this.connection = service.getConnectionWith(this.connectionId)!;
		this.oldTarget = this.connection.target;
		var targetNode = service.getNodeWith(this.newTargetEndpoint.nodeId)!;
		this.connection.target = targetNode.searchEndpointOrCreate(this.newTargetEndpoint.channel, this.newTargetEndpoint.isSource);
	}


}

