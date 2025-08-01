/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { EditorConnection } from "../editor-connection.model";
import { EditorEndpoint } from "../editor-endpoint.model";
import { TopologyEditorService } from "../topology.service";
import { AbstractCompositeAction } from "./abstract-composite.action";
import { AddConnectionAction } from "./add-connection.action";
import { ChangeConnectionAction } from "./change-connection.action";
import { RemoveNodeAction } from "./remove-node.action";


/**
 * An actin to disable the notification from a connection.
 */
export class DisableConnectionNotificationsAction extends AbstractCompositeAction implements ChangeConnectionAction {

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
	}

	/**
	 * Remove the connection. 
	 */
	public override redo(service: TopologyEditorService): void {

		if (this.actions.length > 0) {

			super.redo(service);

		} else {
debugger
			const connection = service.getConnectionWith(this.connectionId)!;
			const notificationNodeId = connection.notificationNodeId!;
			var newSource: EditorEndpoint;
			var newTarget: EditorEndpoint;
			for (var definedConnection of service.connections) {

				if (!definedConnection.isNotification) {

					if (definedConnection.source.nodeId == notificationNodeId) {

						newTarget = definedConnection.target;

					} else if (definedConnection.target.nodeId == notificationNodeId) {

						newSource = definedConnection.source;
					}
				}

			}

			this.addAndRedo(new RemoveNodeAction(notificationNodeId), service);

			const newConnection = new EditorConnection(service.nextConnectionId, newSource!, newTarget!);
			newConnection.type = connection.type;
			newConnection.convertCode = connection.convertCode;
			this.addAndRedo(new AddConnectionAction(newConnection), service);

		}
	}

}

