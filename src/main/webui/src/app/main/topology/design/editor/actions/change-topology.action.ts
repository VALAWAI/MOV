/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorNode } from "../editor-node.model";
import { EditorConnection } from "../editor-connection.model";
import { EditorTopologyService, TopologyChangeAction } from "../editor-topology.service";
import { MinTopology, Topology } from "@app/shared/mov-api";

/**
 * A set of collection to do at the same time.
 */
export class ChangeTopologyAction extends TopologyChangeAction {

	/**
	 * The previous min topology information.
	 */
	public oldMin: MinTopology | null = null;

	/**
	 * The next min topology information.
	 */
	public newMin: MinTopology;

	/**
	 * The previous topology nodes.
	 */
	public oldNodes: EditorNode[] | null = null;

	/**
	 * The new topology nodes.
	 */
	public newNodes: EditorNode[];

	/**
	 * The previous topology connections.
	 */
	public oldConnections: EditorConnection[] | null = null;

	/**
	 * The new topology connections.
	 */
	public newConnections: EditorConnection[];


	/**
	 * Create the action to chnage the layout of the nodes. 
	 */
	constructor(model: Topology) {

		super();

		this.newMin = new MinTopology();
		this.newMin.id = model.id;
		this.newMin.name = model.name;
		this.newMin.description = model.description;
		this.newNodes = [];
		this.newConnections = [];

		// define the nodes and the connections
		if (model.nodes != null) {

			for (var topologyNode of model.nodes) {

				var node = new EditorNode(topologyNode.tag);
				node.position = topologyNode.position;
				node.component = topologyNode.component;
				this.newNodes.push(node);
			}
		}

		if (model.connections != null) {

			for (var topologyConnection of model.connections) {

				var source = this.newNodes.find(n => n.id == topologyConnection.source!.nodeTag)!;
				var sourceEndpoint = source.searchEndpointOrCreate(topologyConnection.source!.channel, true);

				if (topologyConnection.notificationPosition != null) {

					var targetId = this.nextIdFor(this.newNodes, "node");
					var target = new EditorNode(targetId, sourceEndpoint);
					this.newNodes.push(target);
					var targetEndpoint = target.searchEndpointOrCreate(null, true);

					var connectionId = this.nextIdFor(this.newConnections, "connection");
					var connection = new EditorConnection(connectionId, sourceEndpoint, targetEndpoint);
					this.newConnections.push(connection);
					sourceEndpoint = target.searchEndpointOrCreate(null, false);

					if (topologyConnection.notifications != null) {

						for (var notification of topologyConnection.notifications) {

							var target = this.newNodes.find(n => n.id == notification.target!.nodeTag)!;
							var targetEndpoint = target.searchEndpointOrCreate(notification.target!.channel!, false);

							connectionId = this.nextIdFor(this.newConnections, "connection");
							connection = new EditorConnection(connectionId, sourceEndpoint, targetEndpoint, true);
							this.newConnections.push(connection);
						}

					}

				}

				var target = this.newNodes.find(n => n.id == topologyConnection.target!.nodeTag)!;
				var targetEndpoint = target.searchEndpointOrCreate(topologyConnection.target!.channel!, false);

				var connectionId = this.nextIdFor(this.newConnections, "connection");
				var connection = new EditorConnection(connectionId, sourceEndpoint, targetEndpoint);
				this.newConnections.push(connection);
			}
		}
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
	public override undo(service: EditorTopologyService): void {

		service.min = this.oldMin!;
		service.nodes = this.oldNodes!;
		service.connections = this.oldConnections!;

	}

	/**
	 * Restore the topology.
	 */
	public override redo(service: EditorTopologyService): void {

		this.oldMin = service.min;
		this.oldNodes = service.nodes;
		this.oldConnections = service.connections;

		service.min = this.newMin;
		service.nodes = this.newNodes;
		service.connections = this.newConnections;
	}
}

