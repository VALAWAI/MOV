/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { inject, Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { ConfigService } from "@app/shared";
import { DesignTopologyConnection, MinTopology, Topology, TopologyConnectionEndpoint, TopologyNode, toTopologyGraphConnectionType } from "@app/shared/mov-api";
import { EditorNode } from "./editor-node.model";
import { EditorConnection } from "./editor-connection.model";
import { TopologyConnectionNotification } from "@app/shared/mov-api/design/topologies/iopology-connection-notification.model";
import { EditorEndpoint } from "./editor-endpoint.model";
import { TopologyEditorChangedEvent } from "./topology.event";
import { TopologyEditorAction } from "./topology.action";


/**
 * The service that mantains the topology that is editing.
 */
@Injectable()
export class TopologyEditorService {

	/**
	 * The information of the topology.
	 */
	public min: MinTopology = new MinTopology();

	/**
	 * The nodes of the topology.
	 */
	private _nodes: EditorNode[] = [];

	/**
	 * The connections of the topology.
	 */
	private _connections: EditorConnection[] = [];

	/**
	 * The configuration of the application.
	 */
	private readonly conf = inject(ConfigService);

	/**
	 * The changes that has been done in the topology.
	 */
	private changes: TopologyEditorAction[] = [];

	/**
	 * The index of the last actin applied.
	 */
	private lastDoneActionIndex: number = -1;

	/**
	 * The index of the last actin that is stored.
	 */
	private storedIndex: number = -1;

	/**
	 * The subject that mange the changes on the node.
	 */
	private changedSubject = new Subject<TopologyEditorChangedEvent>();

	/**
	 * The index of teh last created node.
	 */
	private lastNodeIndex = 0;

	/**
	 * The index of teh last created connection.
	 */
	private lastConnectionIndex = 0;

	/**
	 * Listen for cnaged in the node.
	 */
	public get changed$(): Observable<TopologyEditorChangedEvent> {

		return this.changedSubject.asObservable();

	};

	/**
	 * Apply the change ans store it.
	 */
	public apply(action: TopologyEditorAction) {

		try {

			this.add(action);
			this.lastDoneActionIndex = this.changes.length - 1;
			action.redo(this);

		} catch (e) {
			console.error(e);
		}
	}

	/**
	 * Store a changed done in the topology.
	 */
	public add(action: TopologyEditorAction) {

		var diff = this.changes.length - (this.lastDoneActionIndex + 1);
		if (diff > 0) {

			this.changes.splice(this.lastDoneActionIndex + 1, diff);
		}
		this.changes.push(action);
		this.lastDoneActionIndex++;
		diff = this.changes.length - this.conf.editorMaxHistory;
		if (this.lastDoneActionIndex > this.conf.editorMaxHistory) {

			this.changes.splice(0, diff);
			this.lastDoneActionIndex = this.changes.length;
		}
	}

	/**
	 * Check if the topology is unsaved.
	 */
	public get unsaved(): boolean {

		return this.min.id == null || this.storedIndex != this.lastDoneActionIndex;

	}

	/**
	 * Check if the current topology is empty.
	 */
	public get isEmpty(): boolean {

		return this.min.id == null && this.min.name == null && this.min.description == null &&
			this.nodes.length == 0 && this.connections.length == 0;

	}

	/**
	 * Called when the topology has been stored.
	 */
	public stored(id: string) {

		this.min.id = id;
		this.storedIndex = this.lastDoneActionIndex;
	}

	/**
	 * Check if can undo.
	 */
	public get canUndo(): boolean {

		return (this.lastDoneActionIndex > -1);
	}

	/**
	 * Undo the last action.
	 */
	public undo(): boolean {

		if (this.canUndo) {

			try {

				var action = this.changes[this.lastDoneActionIndex];
				action.undo(this);
				this.lastDoneActionIndex--;
				this.changedSubject.next({ type: 'UNDO', id: null });
				return true;

			} catch (e) {
				console.error(e);
			}
		}

		return false;

	}

	/**
	 * Check if can redo.
	 */
	public get canRedo(): boolean {

		return (this.lastDoneActionIndex < this.changes.length - 1);
	}

	/**
	 * Redo the last action.
	 */
	public redo(): boolean {

		if (this.canRedo) {

			try {

				var action = this.changes[this.lastDoneActionIndex + 1];
				action.redo(this);
				this.lastDoneActionIndex++;
				this.changedSubject.next({ type: 'REDO', id: null });
				return true;

			} catch (e) {
				console.error(e);
			}
		}

		return false;
	}

	/**
	 * Return the curent topology model.
	 */
	public get model(): Topology {

		var model = new Topology();
		model.id = this.min.id;
		model.name = this.min.name;
		model.description = this.min.description;
		model.nodes = [];
		for (var node of this.nodes) {

			if (node.sourceNotification == null && node.component != null && node.component.id != null) {

				var modelNode = new TopologyNode();
				modelNode.tag = node.id;
				modelNode.position = node.position;
				modelNode.component = node.component;
				model.nodes.push(modelNode);
			}

		}
		model.connections = [];
		var partial: EditorConnection[] = [];
		for (var connection of this.connections) {

			if (connection.source.channel != null) {

				var modelConnection = new DesignTopologyConnection();
				modelConnection.source = new TopologyConnectionEndpoint();
				modelConnection.source.nodeTag = connection.source.nodeId;
				modelConnection.source.channel = connection.source.channel;
				modelConnection.convertCode = connection.convertCode;
				modelConnection.type = toTopologyGraphConnectionType(connection.type);

				if (connection.target.channel != null) {

					modelConnection.target = new TopologyConnectionEndpoint();
					modelConnection.target.nodeTag = connection.target.nodeId;
					modelConnection.target.channel = connection.target.channel;

				}
				model.connections.push(modelConnection);

			} else {

				partial.push(connection);
			}
		}

		for (var connection of partial) {

			var notificationNode = this.getNodeWith(connection.source.id)!;
			for (var modelConnection of model.connections) {

				if (notificationNode.sourceNotification?.nodeId == modelConnection.source?.nodeTag
					&& notificationNode.sourceNotification?.channel == modelConnection.source?.channel
				) {

					if (connection.isNotification) {

						if (modelConnection.notifications == null) {

							modelConnection.notifications = [];
						}
						var notification = new TopologyConnectionNotification();
						notification.convertCode = connection.convertCode;
						notification.type = toTopologyGraphConnectionType(connection.type);
						notification.target = new TopologyConnectionEndpoint();
						notification.target.nodeTag = connection.target.nodeId;
						notification.target.channel = connection.target.channel;
						modelConnection.notifications.push(notification);

					} else {

						modelConnection.target = new TopologyConnectionEndpoint();
						modelConnection.target.nodeTag = connection.target.nodeId;
						modelConnection.target.channel = connection.target.channel;
					}
					break;
				}

			}
		}

		return model;
	}


	/**
	 * Return the node associated to an identifier. 
	 */
	public getNodeWith(id: string | null | undefined): EditorNode | null {

		if (id) {

			return this.nodes.find(n => n.id === id) || null;
		}

		return null;

	}

	/**
	 * Return the connection associated to an identifier. 
	 */
	public getConnectionWith(id: string | null | undefined): EditorConnection | null {

		if (id) {

			return this.connections.find(c => c.id === id) || null;
		}

		return null;

	}

	/**
	 * Return the endpoint associated to an identifier. 
	 */
	public getEndpointWith(id: string | null | undefined): EditorEndpoint | null {

		if (id) {

			for (var node of this.nodes) {

				var endpoint = node.endpoints.find(n => n.id === id) || null;
				if (endpoint != null) {

					return endpoint;
				}
			}
		}

		return null;

	}


	/**
	 * Return the next node identifier to use.
	 */
	public get nextNodeId(): string {

		return 'node_' + (++this.lastNodeIndex);
	}


	/**
	 * Return the next mconnection identifier to use.
	 */
	public get nextConnectionId(): string {

		return 'connection_' + (++this.lastConnectionIndex);
	}

	/**
	 * Return the nodes of the topology.
	 */
	public get nodes(): EditorNode[] {

		return this._nodes;
	}

	/**
	 * Chaneg the nodes of the topology.
	 */
	public set nodes(nodes: EditorNode[]) {

		this._nodes = nodes;
		this.lastNodeIndex = 0;
		for (var node of nodes) {

			var index = Number(node.id.replaceAll(/\D/g, ''));
			if (!isNaN(index) && this.lastNodeIndex < index) {

				this.lastNodeIndex = index;
			}
		}
	}

	/**
	 * Return the connections of the topology.
	 */
	public get connections(): EditorConnection[] {

		return this._connections;
	}

	/**
	 * Chaneg the connections of the topology.
	 */
	public set connections(connections: EditorConnection[]) {

		this._connections = connections;
		this.lastConnectionIndex = 0;
		for (var connection of connections) {

			var index = Number(connection.id.replaceAll(/\D/g, ''));
			if (!isNaN(index) && this.lastConnectionIndex < index) {

				this.lastConnectionIndex = index;
			}
		}
	}


	/**
	 * Return the connection associated to the specified end points. 
	 */
	public getConnectionBetween(source: EditorEndpoint, target: EditorEndpoint): EditorConnection | null {

		return this.connections.find(c => c.source.id === source.id && c.target.id === target.id) || null;

	}

	/**
	 * Notify that a connection has changed.
	 */
	public notifyChangedConnection(id: string) {

		this.changedSubject.next({ type: 'CHANGED_CONNECTION', 'id': id });
	}

	/**
	 * Notify that a connection has removed.
	 */
	public notifyRemovedConnection(id: string) {

		this.changedSubject.next({ type: 'REMOVED_CONNECTION', 'id': id });
	}

	/**
	 * Notify that a connection has added.
	 */
	public notifyAddedConnection(id: string) {

		this.changedSubject.next({ type: 'ADDED_CONNECTION', 'id': id });
	}

	/**
	 * Notify that a node has changed.
	 */
	public notifyChangedNode(id: string) {

		this.changedSubject.next({ type: 'CHANGED_NODE', 'id': id });
	}

	/**
	 * Notify that a node has removed.
	 */
	public notifyRemovedNode(id: string) {

		this.changedSubject.next({ type: 'REMOVED_NODE', 'id': id });
	}

	/**
	 * Notify that a node has added.
	 */
	public notifyAddedNode(id: string) {

		this.changedSubject.next({ type: 'ADDED_NODE', 'id': id });
	}
	/**
	 * Notify that a topology has changed.
	 */
	public notifyChangedTopology() {

		this.changedSubject.next({ type: 'CHANGED_TOPOLOGY', id: null });
	}


}
