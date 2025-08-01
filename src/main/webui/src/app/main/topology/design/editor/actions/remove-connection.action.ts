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
		if (this.actions.length > 0) {

			super.redo(service);

		} else if (!this.connection.isNotification) {

			const notificationNodeId = this.connection.notificationNodeId;
			if (notificationNodeId != null) {
				//must remove the notification node and its connections
				const connectionToRemove = service.connections.filter(c => c.notificationNodeId == notificationNodeId);
				connectionToRemove.forEach(c => this.addAndRedo(new RemoveConnectionAction(c.id), service));
				if (service.getNodeWith(notificationNodeId) != null) {

					this.addAndRedo(new RemoveNodeAction(notificationNodeId), service);

				}// else already removed
			}
		}

	}

}

