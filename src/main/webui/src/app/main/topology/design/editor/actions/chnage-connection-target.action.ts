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
 * An actin taht change a node.
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
	constructor(public override connectionId: string, private targetEndpoiuntId: string) {

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

		this.connection = service.connections.find(c => c.id === this.connectionId)!;
		for (var node of service.nodes) {

			var endpoint = node.endpoints.find(e => e.id == this.targetEndpoiuntId) || null;
			if (endpoint != null) {

				this.oldTarget = this.connection.target;
				this.connection.target = endpoint;
				break;
			}
		}

	}


}

