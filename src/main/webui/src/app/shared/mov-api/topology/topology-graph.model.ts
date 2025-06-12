/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyGraphConnection } from "./topology-graph-connection.model";
import { TopologyGraphNode } from "./topology-graph-node.model";


/**
 * A topology graph that define a posible topology to ue in the MOV.
 *
 * @author VALAWAI
 */
export class TopologyGraph {

	/**
	 * The identifier of the graph.
	 */
	public id: string | null = null;

	/**
	 * The name of the topology graph.
	 */
	public name: string | null = null;

	/**
	 * The description of the topology graph.
	 */
	public description: string | null = null;

	/**
	 * The nodes that form the topology graph.
	 */
	public nodes: TopologyGraphNode[] = [];

	/**
	 * The connections between the nodes in the topology graph.
	 */
	public connections: TopologyGraphConnection[] = [];


}
