/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { MinTopology, Topology } from "@app/shared/mov-api";
import { EditorNode } from './editor-node.model';
import { EditorConnection } from './editor-connection.model';

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
				node.id = 'node_' + this.nodes.length;
				this.nodes.push(node);
			}
		}


		this.unsaved = this.topology.id != null;
	}
	
	/**
	 * 
	 */

} 