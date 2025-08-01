/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { EditorNode } from "../editor-node.model";
import { TopologyEditorService } from "../topology.service";
import { AbstractCompositeAction } from "./abstract-composite.action";
import { ChangeConnectionAction } from "./change-connection.action";
import { AddNodeAction } from "./add-node.action";
import { EditorConnection } from "../editor-connection.model";
import { AddConnectionAction } from "./add-connection.action";
import { RemoveConnectionAction } from "./remove-connection.action";


/**
 * An actin to enable the notification into a connection.
 */
export class EnableConnectionNotificationsAction extends AbstractCompositeAction implements ChangeConnectionAction {


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

			const connection = service.getConnectionWith(this.connectionId)!;
			this.addAndRedo(new RemoveConnectionAction(this.connectionId), service);

			const source = service.getNodeWith(connection.source.nodeId)!;
			const target = service.getNodeWith(connection.target.nodeId)!;

			const notificationNode = new EditorNode(service.nextNodeId, connection.source);
			const notificationSource = notificationNode.searchEndpointOrCreate(null, true)
			const notificationTarget = notificationNode.searchEndpointOrCreate(null, false)
			notificationNode.position = source.calculateMiddlePointTo(target);


			this.addAndRedo(new AddNodeAction(notificationNode), service);

			const notificationToTarget = new EditorConnection(service.nextConnectionId, notificationSource, connection.target, false);
			notificationToTarget.type = connection.type;
			notificationToTarget.convertCode = connection.convertCode;
			const sourceToNotification = new EditorConnection(service.nextConnectionId, connection.source, notificationTarget, false);
			sourceToNotification.type = connection.type;
			sourceToNotification.convertCode = connection.convertCode;
			this.addAndRedo(new AddConnectionAction(sourceToNotification,notificationToTarget), service);
		}


	}

}

