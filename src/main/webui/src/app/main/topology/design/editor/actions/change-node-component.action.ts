/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ChangeNodeAction } from "./change-node.action";
import { TopologyEditorService } from "../topology.service";
import { ComponentDefinition, matchPayloadSchema } from "@app/shared/mov-api";
import { EditorEndpoint } from "../editor-endpoint.model";
import { RemoveConnectionAction } from "./remove-connection.action";
import { ChangeConnectionTargetAction } from "./change-connection-target.action";
import { ChangeConnectionSourceAction } from "./change-connection-source.action";
import { AbstractCompositeAction } from "./abstract-composite.action";


/**
 * An actoin to change the component defined in a node.
 */
export class ChangeNodeComponentAction extends AbstractCompositeAction implements ChangeNodeAction {

	/**
	 * The previopus position.
	 */
	private oldComponent: ComponentDefinition | null = null;

	/**
	 * The previopus position.
	 */
	private oldEndpoints: EditorEndpoint[] = [];

	/**
	 * Create the event with the new component for the node.
	 */
	constructor(public nodeId: string, public newComponent: ComponentDefinition) {

		super();
	}


	/**
	 * Set the new position.
	 */
	public override redo(service: TopologyEditorService): void {

		var node = service.getNodeWith(this.nodeId)!;
		this.oldComponent = node.component;
		this.oldEndpoints = node.endpoints;
		node.component = this.newComponent;
		node.endpoints = [];
		this.actions = [];
		service.notifyChangedNode(this.nodeId);

		if (this.oldEndpoints.length > 0) {

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

							if (connection.source.nodeId == node.id) {

								this.addAndRedo(new ChangeConnectionSourceAction(connection.id, newEndpoint), service);

							} else if (connection.target.nodeId == node.id) {

								this.addAndRedo(new ChangeConnectionTargetAction(connection.id, newEndpoint), service);
							}
						}
						break;
					}
				}

				if (!found) {
					// the endpoint can not exist => remove all the connections that has the endpoint
					const removeConnections = service.connections.filter(c => c.source.id == oldEndpoint.id || c.target.id == oldEndpoint.id);
					removeConnections.forEach(c => this.addAndRedo(new RemoveConnectionAction(c.id), service));
				}
			}
		}
	}

	/**
	 * Restore the old position.
	 */
	public override undo(service: TopologyEditorService): void {

		super.undo(service);
		var node = service.getNodeWith(this.nodeId)!;
		node.component = this.oldComponent;
		node.endpoints = this.oldEndpoints;

	}

}

