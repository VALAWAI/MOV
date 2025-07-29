/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { inject, Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { ConfigService } from "@app/shared";
import { MinTopology, Topology, TopologyNode, DesignTopologyConnection, toTopologyGraphConnectionType, TopologyConnectionEndpoint } from "@app/shared/mov-api";
import { EditorNode } from "./editor-node.model";
import { EditorConnection } from "./editor-connection.model";
import { TopologyConnectionNotification } from "@app/shared/mov-api/design/topologies/iopology-connection-notification.model";


/**
 * The service that mantains the topology that is editing.
 */
@Injectable()
export class EditorTopologyService {

	/**
	 * The information of the topology.
	 */
	public min: MinTopology = new MinTopology();

	/**
	 * The nodes of the topology.
	 */
	public nodes: EditorNode[] = [];

	/**
	 * The connections of the topology.
	 */
	public connections: EditorConnection[] = [];

	/**
	 * The configuration of the application.
	 */
	private readonly conf = inject(ConfigService);

	/**
	 * The changes that has been done in the topology.
	 */
	private changes: TopologyChangeAction[] = [];

	/**
	 * The index of the last actin applied.
	 */
	private changeIndex: number = -1;

	/**
	 * The index of the last actin that is stored.
	 */
	private storedIndex: number = -1;

	/**
	 * The subject that mange the changes on the node.
	 */
	private topologyChangedSubject = new Subject<TopologyChangeAction>();

	/**
	 * Listen for cnaged in the node.
	 */
	public get topologyChanged$(): Observable<TopologyChangeAction> {

		return this.topologyChangedSubject.asObservable();

	};

	/**
	 * Apply the change ans store it.
	 */
	public apply(action: TopologyChangeAction) {

		try {

			this.add(action);
			action.redo(this);
			this.topologyChangedSubject.next(action);

		} catch (e) {
			console.error(e);
		}
	}

	/**
	 * Store a changed done in the topology.
	 */
	public add(action: TopologyChangeAction) {

		var diff = this.changes.length - (this.changeIndex + 1);
		if (diff > 0) {

			this.changes.splice(this.changeIndex + 1, diff);
		}
		this.changes.push(action);
		this.changeIndex++;
		diff = this.changes.length - this.conf.editorMaxHistory;
		if (this.changeIndex > this.conf.editorMaxHistory) {

			this.changes.splice(0, diff);
			this.changeIndex = this.changes.length;
		}
	}

	/**
	 * Check if the topology is unsaved.
	 */
	public get unsaved(): boolean {

		return this.min.id == null || this.storedIndex != this.changeIndex;

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
		this.storedIndex = this.changeIndex;
	}

	/**
	 * Check if can undo.
	 */
	public get canUndo(): boolean {

		return (this.changeIndex > -1);
	}

	/**
	 * Undo the last action.
	 */
	public undo(): boolean {

		if (this.canUndo) {

			try {

				var action = this.changes[this.changeIndex];
				action.undo(this);
				this.changeIndex--;
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

		return (this.changeIndex < this.changes.length - 1);
	}

	/**
	 * Redo the last action.
	 */
	public redo(): boolean {

		if (this.canRedo) {

			try {

				var action = this.changes[this.changeIndex + 1];
				action.redo(this);
				this.changeIndex++;
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

			if (node.sourceNotification == null) {

				var modelNode = new TopologyNode();
				modelNode.tag = node.id;
				modelNode.position = node.position;
				modelNode.component = node.component;
				model.nodes.push(modelNode);
			}

		}
		model.connections = [];
		for (var connection of this.connections) {

			for (var modelConnection of model.connections) {



			}

			var modelConnection = new DesignTopologyConnection();
			if (modelConnection.source == null) {

				modelConnection.source = new TopologyConnectionEndpoint();
				modelConnection.source.nodeTag = connection.source.nodeId;

			}
			if (connection.source.channel != null) {

				modelConnection.source.nodeTag = connection.source.nodeId;
				modelConnection.source.channel = connection.source.channel;
			}
			if (connection.source) {

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

				modelConnection.convertCode = connection.convertCode;
				modelConnection.type = toTopologyGraphConnectionType(connection.type);
				modelConnection.target = new TopologyConnectionEndpoint();
				modelConnection.target.nodeTag = connection.target.nodeId;
				modelConnection.target.channel = connection.target.channel;
			}
			model.connections.push(modelConnection);
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
	 * Return the next node identifier to use.
	 */
	public get nextNodeId(): string {

		var index = this.nodes.length + 1;
		var id = 'node_' + index;
		while (this.getNodeWith(id) != null) {

			index++;
			id = 'node_' + index;
		}
		return id;
	}


	/**
	 * Return the next mconnection identifier to use.
	 */
	public get nextConnectionId(): string {

		var index = this.connections.length + 1;
		var id = 'connection_' + index;
		while (this.getConnectionWith(id) != null) {

			index++;
			id = 'connection_' + index;
		}
		return id;
	}

}

/**
 * An event thgat change the topology.
 */
export abstract class TopologyChangeAction {

	/**
	 * Undo the topology change action.
	 */
	public abstract undo(service: EditorTopologyService): void;

	/**
	 * Redo the topology change action.
	 */
	public abstract redo(service: EditorTopologyService): void;

}

