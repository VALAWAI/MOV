/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ChangeNodeAction } from "./change-node.action";
import { EditorTopologyService, TopologyChangeAction } from "../editor-topology.service";
import { ComponentDefinition, matchPayloadSchema } from "@app/shared/mov-api";
import { EditorEndpoint } from "../editor-endpoint.model";
import { CollectionAction } from "./collection.action";
import { RemoveConnectionAction } from "./remove-connection.action";
import { RemoveNodeAction } from "./remove-node.action";
import { ChangeConnectionTargetAction } from "./change-connection-target.action";
import { ChangeConnectionSourceAction } from "./change-connection-source.action";

/**
 * An actoin to change the component defined in a node.
 */
export class ChangeNodeComponentAction extends ChangeNodeAction {

	/**
	 * The previopus position.
	 */
	private oldComponent: ComponentDefinition | null = null;

	/**
	 * The previopus position.
	 */
	private oldEndpoints: EditorEndpoint[] = [];

	/**
	 * The actions That adapt the topology to the new component.
	 */
	private adaptTopologyActions: CollectionAction | null = null;

	/**
	 * Create the event with the node that changed.
	 */
	constructor(public nodeId: string, public newComponent: ComponentDefinition) {

		super();
	}

	/**
	 * Set the new position.
	 */
	public override redo(service: EditorTopologyService): void {

		var node = service.getNodeWith(this.nodeId)!;
		this.oldComponent = node.component;
		this.oldEndpoints = node.endpoints;
		node.component = this.newComponent;
		node.endpoints = [];

		if (this.oldEndpoints.length > 0) {

			var adaptActions: TopologyChangeAction[] = [];
			const removedIds = new Set<string>();
			for (var oldEndpoint of this.oldEndpoints) {

				var oldChannel = this.oldComponent!.channels!.find(c => c.name == oldEndpoint.channel)!;
				var found = false;
				for (var newChannel of this.newComponent.channels || []) {

					if (matchPayloadSchema(oldChannel.publish, newChannel.publish)
						&& matchPayloadSchema(oldChannel.subscribe, newChannel.subscribe)
					) {
						//exist an equivalent endpoint => change it on the connection
						found = true;
						var newEndpoint = node.searchEndpointOrCreate(newChannel.name, newChannel.publish != null);
						for (var connection of service.connections) {

							if (!removedIds.has(connection.id)) {

								if (connection.source.nodeId == node.id) {

									adaptActions.push(new ChangeConnectionSourceAction(connection.id, newEndpoint));

								} else if (connection.target.nodeId == node.id) {

									adaptActions.push(new ChangeConnectionTargetAction(connection.id, newEndpoint));
								}
							}
						}
						break;
					}
				}

				if (!found) {
					// the endpoint can not exist => remove all the connections that has the endpoint
					for (var connection of service.connections) {

						if ((connection.source.id == oldEndpoint.id || connection.target.id == oldEndpoint.id)
							&& removedIds.add(connection.id)
						) {

							adaptActions.push(new RemoveConnectionAction(connection.id));
							if (connection.target.channel == null) {
								//must remove the notification node and its connections
								var notificationNode = service.getNodeWith(connection.target.nodeId)!;
								for (var notificationConnection of service.connections) {

									if (notificationConnection.source.nodeId == notificationNode.id
										&& removedIds.add(notificationConnection.id)
									) {
										adaptActions.push(new RemoveConnectionAction(notificationConnection.id));
									}
								}
								adaptActions.push(new RemoveNodeAction(notificationNode.id));
							}
						}
					}
				}
			}

			if (adaptActions.length > 0) {

				this.adaptTopologyActions = new CollectionAction(adaptActions);
				this.adaptTopologyActions.redo(service);
			}

		} else {
			// Nothing to adapt
			this.adaptTopologyActions = null;
		}
	}

	/**
	 * Restore the old position.
	 */
	public override undo(service: EditorTopologyService): void {

		var node = service.getNodeWith(this.nodeId)!;
		node.component = this.oldComponent;
		node.endpoints = this.oldEndpoints;
		if (this.adaptTopologyActions != null) {

			this.adaptTopologyActions.undo(service);
		}

	}

}

