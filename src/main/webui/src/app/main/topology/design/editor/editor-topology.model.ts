/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ComponentDefinition, ComponentType, DesignTopologyConnection, MinTopology, Point, Topology, TopologyConnectionEndpoint, TopologyNode } from "@app/shared/mov-api";
import { EditorNode } from './editor-node.model';
import { EditorConnection } from './editor-connection.model';
import { EditorEndpoint } from "./editor-endpoint.model";

/**
 * The information of teh update of the topology.
 */
export class UpdateTopologyEvent {

	/**
	 * The nodes that has been removed when the topology is updated.
	 */
	public removedNodes: EditorNode[] = [];

	/**
	 * The connections that has been removed when the topology is updated.
	 */
	public removedConnections: EditorConnection[] = [];

	/**
	 * Check if the update do some removes.
	 */
	public get hasRemovedSomething(): boolean {

		return this.removedNodes.length > 0 || this.removedConnections.length > 0;
	}

	/**
	 * Undo the removed nodes and connections.
	 */
	public undo(topology: EditorTopology) {

		topology.connections.push(...this.removedConnections);
		topology.nodes.push(...this.removedNodes);

	}

}

/**
 * The information of teh update of the topology.
 */
export class UpdateTopologyNodeEvent extends UpdateTopologyEvent {

	/**
	 * The endpoint of the node that has been removed.
	 */
	public originalEndpoints: EditorEndpoint[];

	/**
	 * Create an event to update a node.
	 */
	constructor(public node: EditorNode) {

		super();
		this.originalEndpoints = node.endpoints;
		node.endpoints = [...node.endpoints];
	}

	/**
	 * Undo the removed endpoints.
	 */
	public override undo(topology: EditorTopology) {

		super.undo(topology);
		this.node.endpoints = this.originalEndpoints;
	}

}


/**
 * Define the topology that is editing into the editor.
 */
export class EditorTopology {

	/**
	 * This is {@code true} if the topology is modified and not saved.
	 */
	public unsaved: boolean = false;

	/**
	 * The topology that is editing into the editor.
	 */
	private topology: Topology = new Topology();

	/**
	 * The nodes of the topology.
	 */
	public nodes: EditorNode[] = [];

	/**
	 * The connections of the topology.
	 */
	public connections: EditorConnection[] = [];

	/**
	 * Chaneg the identifier of teh topology.
	 */
	public set id(id: string) {

		this.topology.id = id;
	}

	/**
	 * Called whne change the topology.
	 */
	public set minTopology(topology: MinTopology) {

		this.topology.name = topology.name;
		this.topology.description = topology.description;

	}

	/**
	 * Called whne change the topology.
	 */
	public get minTopology(): MinTopology {

		var min = new MinTopology();
		min.id = this.topology.id;
		min.name = this.topology.name;
		min.description = this.topology.description;
		return min;
	}


	/**
	 * Change the topology to edit.
	 */
	public set model(topology: Topology | null | undefined) {

		if (topology != null) {

			this.topology = topology;

		} else {

			this.topology = new Topology();
		}

		this.nodes = [];
		this.connections = [];
		if (this.topology.nodes != null) {

			for (var topologyNode of this.topology.nodes) {

				var node = new EditorNode(topologyNode);
				this.nodes.push(node);
			}
		}

		if (this.topology.connections != null) {

			for (var topologyConnection of this.topology.connections) {

				var source = this.nodes.find(n => n.id == topologyConnection.source!.nodeTag)!;
				var sourceEndpoint = source.searchEndpointOrCreate(topologyConnection.source!.channel, true);

				if (topologyConnection.notificationPosition != null) {

					var target = new EditorNode(topologyConnection);
					this.nodes.push(target);
					var targetEndpoint = target.searchEndpointOrCreate(null, true);

					var connection = new EditorConnection(topologyConnection, sourceEndpoint, targetEndpoint);
					connection.id = 'connection_' + this.connections.length;
					this.connections.push(connection);
					sourceEndpoint = target.searchEndpointOrCreate(null, false);

					if (topologyConnection.notifications != null) {

						for (var notification of topologyConnection.notifications) {

							var target = this.nodes.find(n => n.id == notification.target!.nodeTag)!;
							var targetEndpoint = target.searchEndpointOrCreate(notification.target!.channel!, false);

							connection = new EditorConnection(topologyConnection, sourceEndpoint, targetEndpoint);
							connection.id = 'connection_' + this.connections.length;
							this.connections.push(connection);
						}

					}

				}

				var target = this.nodes.find(n => n.id == topologyConnection.target!.nodeTag)!;
				var targetEndpoint = target.searchEndpointOrCreate(topologyConnection.target!.channel!, false);


				var connection = new EditorConnection(topologyConnection, sourceEndpoint, targetEndpoint);
				connection.id = 'connection_' + this.connections.length;
				this.connections.push(connection);

			}

		}

		this.unsaved = this.topology.id != null;
	}

	/**
	 * Return a copuy of the topology.
	 */
	public get model(): Topology {

		var model = new Topology();
		model.id = this.topology.id;
		model.name = this.topology.name;
		model.description = this.topology.description;
		model.nodes = [];
		for (var node of this.nodes) {

			if (node.isTopologyNode) {

				model.nodes.push(node.topologyNode);

			}
		}
		model.connections = [];
		for (var connection of this.connections) {

			if (connection.source.channel != null) {

				model.connections.push(connection.model);
			}
		}

		return model;
	}

	/**
	 * Called when want to update the endppoint of a node.
	 */
	public synchronizeNodeEndpoints(node: EditorNode, newEndpoints: EditorEndpoint[]): UpdateTopologyNodeEvent {

		var event = new UpdateTopologyNodeEvent(node);
		node.endpoints = [...newEndpoints];
		node.endpoints.sort((e1, e2) => e1.channel!.localeCompare(e2.channel!));
		for (var endpoint of event.originalEndpoints) {

			var index = newEndpoints.findIndex(e => e.channel === endpoint.channel);
			if (index < 0) {
				// end point remobed
				this.propagateRemovedEndpoint(endpoint, event);
			}
		}

		return event;
	}

	/**
	 * Propagate the changes necessaries to remove the specified end point.
	 */
	public propagateRemovedEndpoint(endpoint: EditorEndpoint, event: UpdateTopologyEvent) {

		var id = endpoint.id;
		var removedConnections: EditorConnection[] = [];
		for (var i = 0; i < this.connections.length; i++) {

			const connection = this.connections[i];
			if (connection.source.id == id || connection.target.id == id) {
				// The connection must be removed
				this.connections.splice(i, 1);
				event.removedConnections.push(connection);
				removedConnections.push(connection);
				i--;
			}
		}

		for (var connection of removedConnections) {

			this.propagateRemovedConnection(connection, event);
		}

	}

	/**
	 * Propagate the changes necessaries when a connection is removed.
	 */
	public propagateRemovedConnection(connection: EditorConnection, event: UpdateTopologyEvent) {

		if (connection.model.notificationPosition != null) {

			for (var i = 0; i < this.nodes.length; i++) {

				var node = this.nodes[i];
				if (node.isNotificationNodeOf(connection)) {

					this.nodes.splice(i, 1);
					event.removedNodes.push(node);
					this.propagateRemovedNode(node, event);
					break;
				}
			}
		}

	}

	/**
	 * Propagate the changes necessaries when a node is removed.
	 */
	public propagateRemovedNode(node: EditorNode, event: UpdateTopologyEvent) {

		for (var endpoint of [...node.endpoints]) {

			this.propagateRemovedEndpoint(endpoint, event);
		}
	}

	/**
	 * Called to remove a node.
	 */
	public removeNode(node: EditorNode | string): UpdateTopologyEvent {

		var nodeId = "";
		if (typeof node === 'string') {

			nodeId = node;

		} else {
			nodeId = node.id;
		}

		var index = this.nodes.findIndex(n => n.id === nodeId);
		if (index > -1) {

			node = this.nodes.splice(index, 1)[0];
			var event = new UpdateTopologyNodeEvent(node);
			event.removedNodes.push(node);
			this.unsaved = true;
			this.propagateRemovedNode(node, event);
			return event;

		} else {

			return new UpdateTopologyEvent();
		}
	}

	/**
	 * Add a node to the topology.
	 */
	public addNodeWithType(type: ComponentType, x: number, y: number): EditorNode {

		var newTopologyNode = new TopologyNode();
		newTopologyNode.tag = 'node_0';
		newTopologyNode.position = new Point();
		newTopologyNode.position.x = x;
		newTopologyNode.position.y = y;
		newTopologyNode.component = new ComponentDefinition();
		newTopologyNode.component.type = type;
		return this.addNodeWithModel(newTopologyNode)
	}

	/**
	 * Add a node to the topology.
	 */
	public addNodeWithModel(newTopologyNode: TopologyNode): EditorNode {

		var newNode = new EditorNode(newTopologyNode);
		var collision = true;
		var id = this.nodes.length + 1;
		while (collision) {

			collision = false;
			for (var dataNode of this.nodes) {

				if (dataNode.id == newNode.id) {

					id++;
					newTopologyNode.tag = 'node_' + id;
					collision = true;
					break;

				} else {

					var distance = Point.distance(dataNode.position, newNode.position);
					if (distance < 64) {

						newNode.position.x += 64;
						newNode.position.y += 64;
						collision = true;
						break;

					}
				}
			}

		}
		this.nodes.push(newNode);
		this.unsaved = true;
		return newNode;
	}


	/**
	 * Add a node to the topology.
	 */
	public addConnectionBetween(sourceEndpointId: string, targetEndpointId: string): EditorConnection {

		var sourceNode: EditorNode;
		var sourceEndpoint: EditorEndpoint;
		var targetNode: EditorNode;
		var targetEndpoint: EditorEndpoint;
		for (var node of this.nodes) {

			for (var endpoint of node.endpoints) {

				if (endpoint.id == sourceEndpointId) {

					sourceEndpoint = endpoint;
					sourceNode = node;
				}
				if (endpoint.id == targetEndpointId) {

					targetEndpoint = endpoint;
					targetNode = node;
				}
			}

		}
		var model = new DesignTopologyConnection();
		model.source = new TopologyConnectionEndpoint();
		model.source.nodeTag = sourceNode!.id;
		model.source.channel = sourceEndpoint!.channel;
		model.target = new TopologyConnectionEndpoint();
		model.target.nodeTag = targetNode!.id;
		model.target.channel = targetEndpoint!.channel;
		var connection = new EditorConnection(model, sourceEndpoint!, targetEndpoint!);
		this.connections.push(connection);
		this.unsaved = true;
		return connection;
	}
} 