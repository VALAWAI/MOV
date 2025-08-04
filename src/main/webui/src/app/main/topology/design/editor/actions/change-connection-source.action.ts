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
 * An actin taht change the source of a connection.
 */
export class ChangeConnectionSourceAction implements ChangeConnectionAction {

	/**
	 * The previous endpoint to teh source.
	 */
	private oldSourceEndpoint: EditorEndpoint | null = null;

	/**
	 * Create the action with the connection to be removed.
	 */
	constructor(public connectionId: string, public newSourceEndpoint: EditorEndpoint) {

	}

	/**
	 * Restore the old source.
	 */
	public undo(service: TopologyEditorService): void {

		const connection = service.getConnectionWith(this.connectionId)!;
		var sourceNode = service.getNodeWith(this.oldSourceEndpoint!.nodeId)!;
		connection.source = sourceNode.searchEndpointOrCreate(this.oldSourceEndpoint!.channel, true);
		service.notifyChangedNode(sourceNode.id);
		service.notifyChangedConnection(this.connectionId);

	}

	/**
	 * Change the connection source. the connection. 
	 */
	public redo(service: TopologyEditorService): void {

		const connection = service.getConnectionWith(this.connectionId)!;
		this.oldSourceEndpoint = connection.source;
		var sourceNode = service.getNodeWith(this.newSourceEndpoint.nodeId)!;
		connection.source = sourceNode.searchEndpointOrCreate(this.newSourceEndpoint.channel, true);
		service.notifyChangedNode(sourceNode.id);
		service.notifyChangedConnection(this.connectionId);
	}


}

