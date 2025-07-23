/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { MinTopology, Topology, TopologyConnectionEndpoint } from "@app/shared/mov-api";
import { EditorNode } from './editor-node.model';
import { EditorConnection } from './editor-connection.model';
import { EditorEndpoint } from "./editor-endpoint.model";

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
				var sourceEndpoint = source.searchEndpointOrCreate(topologyConnection.source!.channel!, true);

				var target = this.nodes.find(n => n.id == topologyConnection.target!.nodeTag)!;
				var targetEndpoint = target.searchEndpointOrCreate(topologyConnection.target!.channel!, false);


				var connection = new EditorConnection(topologyConnection, sourceEndpoint, targetEndpoint);
				connection.id = 'connection_' + this.connections.length;
				this.connections.push(connection);

			}

		}

		this.unsaved = this.topology.id != null;
	}


} 