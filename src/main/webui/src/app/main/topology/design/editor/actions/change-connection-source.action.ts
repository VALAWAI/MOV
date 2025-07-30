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
 * An actin taht change the source of a connection.
 */
export class ChangeConnectionSourceAction extends ChangeConnectionAction {

	/**
	 * The conneciton tha has been chnaged.
	 */
	private connection: EditorConnection | null = null;

	/**
	 * The previous conneciton source.
	 */
	private oldSource: EditorEndpoint | null = null;

	/**
	 * Create the action with the connection to be removed.
	 */
	constructor(public override connectionId: string, public newSourceEndpoint: EditorEndpoint) {

		super();
	}

	/**
	 * Undo the remove.
	 */
	public override undo(service: EditorTopologyService): void {

		this.connection!.source = this.oldSource!;

	}

	/**
	 * Remove the connection. 
	 */
	public override redo(service: EditorTopologyService): void {

		this.connection = service.getConnectionWith(this.connectionId)!;
		this.oldSource = this.connection.source;
		var sourceNode = service.getNodeWith(this.newSourceEndpoint.nodeId)!;
		this.connection.source = sourceNode.searchEndpointOrCreate(this.newSourceEndpoint.channel, this.newSourceEndpoint.isSource);
	}


}

