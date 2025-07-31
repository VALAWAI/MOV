/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { EditorConnection } from "../editor-connection.model";
import { TopologyEditorService } from "../topology.service";
import { AbstractCompositeAction } from "./abstract-composite.action";
import { ChangeConnectionAction } from "./change-connection.action";
import { RemoveNodeAction } from "./remove-node.action";


/**
 * An actin to remove a connection.
 */
export class RemoveConnectionAction extends AbstractCompositeAction implements ChangeConnectionAction {

	/**
	 * The connection that has been removed.
	 */
	private connection: EditorConnection | null = null


	/**
	 * Create the event with the new connection to remove.
	 */
	constructor(public connectionId: string) {

		super();
	}

	/**
	 * Undo the remove.
	 */
	public override undo(service: TopologyEditorService): void {

		super.undo(service);
		service.connections.push(this.connection!);
		service.notifyAddedConnection(this.connectionId);
	}

	/**
	 * Remove the connection. 
	 */
	public override redo(service: TopologyEditorService): void {

		var index = service.connections.findIndex(c => c.id == this.connectionId);
		this.connection = service.connections.splice(index, 1)[0];
		service.notifyRemovedConnection(this.connectionId);
		this.actions = [];

		if (!this.connection.isNotification) {

			var notificationNodeId: string | null = null;
			if (this.connection.source.channel == null) {

				notificationNodeId = this.connection.source.nodeId;

			} else if (this.connection.target.channel == null) {

				notificationNodeId = this.connection.target.nodeId;
			}

			if (notificationNodeId != null) {
				//must remove the notification node and its connections
				for (var connection of [...service.connections]) {

					if (connection.source.nodeId == notificationNodeId || connection.target.nodeId == notificationNodeId) {

						this.addAndRedo(new RemoveConnectionAction(connection.id), service);
					}

				}
				if (service.getNodeWith(notificationNodeId) != null) {

					this.addAndRedo(new RemoveNodeAction(notificationNodeId), service);
				}
			}
		}

	}

}

