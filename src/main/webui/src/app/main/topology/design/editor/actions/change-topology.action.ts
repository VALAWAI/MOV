/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorNode } from "../editor-node.model";
import { EditorConnection } from "../editor-connection.model";
import { TopologyEditorService } from "../topology.service";
import { MinTopology, Topology } from "@app/shared/mov-api";
import { TopologyEditorAction } from "../topology.action";

/**
 * A set of collection to do at the same time.
 */
export class ChangeTopologyAction implements TopologyEditorAction {

	/**
	 * The previous min topology information.
	 */
	public oldMin: MinTopology | null = null;

	/**
	 * The next min topology information.
	 */
	public newMin: MinTopology | null = null;

	/**
	 * The previous topology nodes.
	 */
	public oldNodes: EditorNode[] | null = null;

	/**
	 * The new topology nodes.
	 */
	public newNodes: EditorNode[] = [];

	/**
	 * The previous topology connections.
	 */
	public oldConnections: EditorConnection[] | null = null;

	/**
	 * The new topology connections.
	 */
	public newConnections: EditorConnection[] = [];


	/**
	 * Create the action to chnage the layout of the nodes. 
	 */
	constructor(private model: Topology) {
	}


	/**
	 * Return the next identifier to use.
	 */
	private nextIdFor(elements: (EditorNode | EditorConnection)[], prefix: string): string {

		var index = elements.length + 1;
		var id = prefix + '_' + index;
		while (elements.findIndex(e => e.id == id) > -1) {

			index++;
			id = prefix + '_' + index;
		}
		return id;
	}


	/**
	 * Change the topology.
	 */
	public undo(service: TopologyEditorService): void {

		service.min = this.oldMin!;
		service.nodes = this.oldNodes!;
		service.connections = this.oldConnections!;
		service.notifyChangedTopology();

	}

	/**
	 * Restore the topology.
	 */
	public redo(service: TopologyEditorService): void {

		this.oldMin = service.min;
		this.oldNodes = service.nodes;
		this.oldConnections = service.connections;

		if (this.newMin != null) {

			service.min = this.newMin;
			service.nodes = this.newNodes;
			service.connections = this.newConnections;

		} else {
			// need to load the data
			this.newMin = new MinTopology();
			this.newMin.id = this.model.id;
			this.newMin.name = this.model.name;
			this.newMin.description = this.model.description;
			service.min = this.newMin;

			// define the nodes and the connections
			if (this.model.nodes != null) {

				for (var topologyNode of this.model.nodes) {

					var node = new EditorNode(topologyNode.tag);
					node.position = topologyNode.position;
					node.component = topologyNode.component;
					this.newNodes.push(node);
				}
			}
			service.nodes = this.newNodes;

			service.connections = this.newConnections;
			if (this.model.connections != null) {

				for (var topologyConnection of this.model.connections) {

					var source = service.getNodeWith(topologyConnection.source!.nodeTag)!;
					var sourceEndpoint = source.searchEndpointOrCreate(topologyConnection.source!.channel, true);
					var target = service.getNodeWith(topologyConnection.target!.nodeTag)!;
					var targetEndpoint = target.searchEndpointOrCreate(topologyConnection.target!.channel, false);

					if (topologyConnection.notificationPosition == null
						&& (topologyConnection.notifications == null || topologyConnection.notifications.length == 0)
					) {

						const connection = new EditorConnection(service.nextConnectionId, sourceEndpoint, targetEndpoint);
						connection.type = topologyConnection.type;
						connection.convertCode = topologyConnection.convertCode;
						service.connections.push(connection);

					} else {
						// need a notification node
						const notificationNode = new EditorNode(service.nextNodeId, sourceEndpoint);
						if (topologyConnection.notificationPosition != null) {

							notificationNode.position = topologyConnection.notificationPosition;

						} else {

							notificationNode.position = source.calculateMiddlePointTo(target);
						}
						service.nodes.push(notificationNode);

						const notificationSourceEndpoint = notificationNode.searchEndpointOrCreate(null, true);
						const notificationTargetEndpoint = notificationNode.searchEndpointOrCreate(null, false);
						const sourceToNotification = new EditorConnection(service.nextConnectionId, sourceEndpoint, notificationTargetEndpoint);
						sourceToNotification.type = topologyConnection.type;
						sourceToNotification.convertCode = topologyConnection.convertCode;
						service.connections.push(sourceToNotification);

						const notificationToTarget = new EditorConnection(service.nextConnectionId, notificationSourceEndpoint, targetEndpoint);
						notificationToTarget.type = topologyConnection.type;
						notificationToTarget.convertCode = topologyConnection.convertCode;
						service.connections.push(notificationToTarget);

						if (topologyConnection.notifications != null) {

							for (var notification of topologyConnection.notifications) {

								const notificationTarget = service.getNodeWith(notification.target!.nodeTag)!;
								const notificationTargetEndpoint = notificationTarget.searchEndpointOrCreate(notification.target!.channel, false);
								const notificationConnection = new EditorConnection(service.nextConnectionId, notificationSourceEndpoint, notificationTargetEndpoint, true);
								notificationConnection.type = notification.type;
								notificationConnection.convertCode = notification.convertCode;
								service.connections.push(notificationConnection);

							}
						}

					}
				}
			}
		}

		service.notifyChangedTopology();
	}
}

