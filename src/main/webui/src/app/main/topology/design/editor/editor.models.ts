/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import {
	DesignTopologyConnection,
	MinTopology,
	Topology,
	TopologyConnectionEndpoint,
	TopologyNode
} from "@app/shared/mov-api";

/**
 * An element that can be selected.
 */
export interface TopologyElement {

	/**
	 * The identifier of the element.
	 */
	id: string;

}


/**
 * The input/output data from a node.
 */
export class EndpointData implements TopologyElement {

	/**
	 * The identifier of the connection.
	 */
	public id: string = '';

	/**
	 * This is true if is a end point to publish messages.
	 */
	public isPublished: boolean = false;

	/**
	 * This is true if is a channel exchange data.
	 */
	public isData: boolean = false;

	/**
	 * The name of the component.
	 */
	public name: string = '';

	/**
	 * The name of the component.
	 */
	public description: string | null = null;

	/**
	 * Create the model.
	 */
	constructor(
		public endPoint: TopologyConnectionEndpoint
	) {

		this.id = this.endPoint.nodeTag + '_' + this.endPoint.channel;
		const matches = this.endPoint.channel?.match(/.*\/(data|flow)\/(\w+)/g);
		if (matches && matches.length == 2) {

			this.isData = 'data' == matches[0];
			this.name = matches[1];
		}
	}

}

/**
 * The information of a node of the topology.
 */
export class NodeData implements TopologyElement {

	/**
	 * The inpout/output connections.
	 */
	public endpoints: EndpointData[] = [];


	/**
	 * Create the model
	 */
	constructor(
		public node: TopologyNode
	) { }

	/**
	 * Return the identifeir of the Node.
	 */
	public get id(): string {

		return this.node.tag ||= '';
	}


	/**
	 * Update the encpoints associated to the specified connection.
	 * 
	 */
	public updateEndpointsWith(connectionData: ConnectionData) {

		var data: EndpointData | null = null;
		if (this.node.tag == connectionData.connection.source?.nodeTag) {

			data = new EndpointData(connectionData.connection.source);

		} else if (this.node.tag == connectionData.connection.target?.nodeTag) {

			data = new EndpointData(connectionData.connection.target);
		}

		if (data) {

			if (this.node.component?.channels) {

				for (var channel of this.node.component.channels) {

					if (channel.name == data.endPoint.channel) {

						data.isPublished = channel.publish != null;
						data.description = channel.description;
						break;
					}
				}
			}

			this.endpoints.push(data);
			this.endpoints.sort((a: EndpointData, b: EndpointData) => a.name.localeCompare(b.name));
		}

	}

}

/**
 * The information of a connection of the topology.
 */
export class ConnectionData implements TopologyElement {

	/**
	 * The identifier of the connection.
	 */
	public id: string;

	/**
	 * Create the model
	 */
	constructor(
		public connection: DesignTopologyConnection
	) {

		this.id = '';
		if (this.connection.source != null) {

			this.id += this.connection.source.nodeTag + '_' + this.connection.source.channel;

		}

		this.id += "->";

		if (this.connection.target != null) {

			this.id += this.connection.target.nodeTag + '_' + this.connection.target.channel;

		}
	}
}


/**
 * The model that comntains the graphical view representation of the topology.
 *
 * @author VALAWAI
 */
export class TopologyData {

	/**
	 * The minimum information of the topology.
	 */
	public min: MinTopology = new MinTopology();

	/**
	 * The defined nodes.
	 */
	public nodes: NodeData[] = [];

	/**
	 * The defined connections.
	 */
	public connections: ConnectionData[] = [];

	/**
	 * This is true if the information has changed.
	 */
	public modified: boolean = false;

	/**
	 * Update the topology of the view.
	 */
	public set topology(topology: Topology) {

		this.modified = false;
		this.min.id = topology.id;
		this.min.name = topology.name;
		this.min.description = topology.description;
		this.nodes = [];
		for (var node of topology.nodes) {

			var nodeData = new NodeData(node);
			this.nodes.push(nodeData);

		}

		this.connections = [];
		for (var connection of topology.connections) {

			var connectionData = new ConnectionData(connection);
			this.connections.push(connectionData);
			for (var nodeData of this.nodes) {

				nodeData.updateEndpointsWith(connectionData);
			}

		}

	}

}