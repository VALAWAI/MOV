/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import {
	ComponentType,
	ComponentDefinition,
	DesignTopologyConnection,
	MinTopology,
	Topology,
	TopologyConnectionEndpoint,
	TopologyNode,
	Point,
	ChannelSchema
} from "@app/shared/mov-api";
import { IPoint } from "@foblex/2d";
import { FSelectionChangeEvent } from "@foblex/flow";

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
	 * The name of the endpoint.
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
		if (this.endPoint.channel) {

			const matches = /.+\/c[0|1|2]\/\w+\/([\w|\/]+)/ig.exec(this.endPoint.channel);
			if (matches && matches.length == 2) {

				this.name = matches[1];
				this.isData = this.name.startsWith('data');

			}
		}
	}

	/**
	 * Create the endpoint fro a node and a scehma.
	 * 
	 * @param node of the endpoint.
	 * @param channle for the endpoint.
	 * 
	 * @return the endpoint for the node for the specified schema.
	 */
	public static with(node: NodeData, channel: ChannelSchema): EndpointData {

		var model = new TopologyConnectionEndpoint();
		model.nodeTag = node.model.tag;
		model.channel = channel.name;
		var data = new EndpointData(model);
		data.description = channel.description;
		data.isPublished = channel.publish != null;
		return data;
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
		public model: TopologyNode
	) { }

	/**
	 * Return the identifeir of the Node.
	 */
	public get id(): string {

		return this.model.tag || '';
	}

	/**
	 * Change the identifeir of the Node.
	 */
	public set id(id: string) {

		this.model.tag = id;
	}

	/**
	 * Return the poition of the node.
	 */
	public get position(): IPoint {

		return this.model.position;
	}

	/**
	 * Change the node position..
	 */
	public set position(point: IPoint) {

		this.model.position = point;
	}

	/**
	 * The type of node.
	 */
	public get type(): ComponentType {

		return this.model.component?.type || 'C0';
	}

	/**
	 * The name of node.
	 */
	public get name(): string {

		return this.model.component?.name || '';
	}

	/**
	 * REturn the type and name of the compomnent.
	 */
	public get typeName(): string {

		return this.type + ' ' + this.name;
	}


	/**
	 * Update the encpoints associated to the specified connection.
	 * 
	 * @param connection where the node may be it is the source or the target.
	 */
	public updateEndpointsWith(connection: ConnectionData) {

		var data: EndpointData | null = null;
		if (this.model.tag == connection.model.source?.nodeTag) {

			data = new EndpointData(connection.model.source);

		} else if (this.model.tag == connection.model.target?.nodeTag) {

			data = new EndpointData(connection.model.target);
		}

		if (data) {

			if (this.model.component?.channels) {

				for (var channel of this.model.component.channels) {

					if (channel.name == data.endPoint.channel) {

						data.isPublished = channel.publish != null;
						data.description = channel.description;
						break;
					}
				}
			}

			this.addEndpoint(data);
		}

	}

	/**
	 * Add an endpoint into the node.
	 */
	public addEndpoint(data: EndpointData) {

		this.endpoints.push(data);
		this.endpoints.sort((a: EndpointData, b: EndpointData) => a.name.localeCompare(b.name));
	}

	/**
	 * Change the endpoints of the node.
	 */
	public chnageEndpoints(data: EndpointData[]) {

		this.endpoints = data;
		this.endpoints.sort((a: EndpointData, b: EndpointData) => a.name.localeCompare(b.name));
	}

	/**
	 * Return the posible end points.
	 */
	public possibleEndpoint(): EndpointData[] {

		var possible: EndpointData[] = [];
		if (this.model.component?.channels) {

			CHANNEL: for (var channel of this.model.component.channels) {

				for (var endpoint of this.endpoints) {

					if (endpoint.endPoint.channel == channel.name) {

						continue CHANNEL;
					}
				}

				var endpoint = EndpointData.with(this, channel);
				possible.push(endpoint);
			}
		}

		return possible;
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
		public model: DesignTopologyConnection
	) {

		this.id = '';
		if (this.model.source != null) {

			this.id += this.model.source.nodeTag + '_' + this.model.source.channel;

		}

		this.id += "->";

		if (this.model.target != null) {

			this.id += this.model.target.nodeTag + '_' + this.model.target.channel;

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
	 * Create a new topology data.
	 */
	public constructor(topology: Topology | null = null) {

		this.nodes = [];
		this.connections = [];

		if (topology != null) {

			this.min.id = topology.id;
			this.min.name = topology.name;
			this.min.description = topology.description;

			if (topology.nodes) {

				for (var node of topology.nodes) {

					this.addNodeWithModel(node);
				}

				if (topology.connections) {

					for (var model of topology.connections) {

						var connection = this.addConnectionWithModel(model);
						for (var nodeData of this.nodes) {

							nodeData.updateEndpointsWith(connection);
						}

					}
				}
			}
		}

	}

	/**
	 * Return the identifeir of the topology.
	 */
	public get id(): string | null {

		return this.min.id;
	}

	/**
	 * Change the identifeir of the topology.
	 */
	public set id(id: string | null) {

		this.min.id = id;
	}

	/**
	 * Return teh node associated to the specifried identifier.
	 */
	public getNodeWithId(id: string): NodeData | null {

		for (let node of this.nodes) {

			if (node.id == id) {

				return node;
			}
		}

		return null;
	}

	/**
	 * Return teh connection associated to the specifried identifier.
	 */
	public getConnectionWithId(id: string): ConnectionData | null {

		for (let connection of this.connections) {

			if (connection.id == id) {

				return connection;
			}
		}

		return null;
	}


	/**
	 * Return the element associated to the selection event. 
	 */
	public getElementFor(event: FSelectionChangeEvent): TopologyElement | null {

		if (event.fNodeIds.length > 0) {

			const selectedNodeId = event.fNodeIds[0];
			return this.getNodeWithId(selectedNodeId);

		} else if (event.fConnectionIds.length > 0) {

			const selectedConnectionId = event.fConnectionIds[0];
			return this.getConnectionWithId(selectedConnectionId);

		} else {

			return null;
		}

	}

	/**
	 * Add a node to the topology.
	 */
	public addNodeWithType(type: ComponentType, x: number, y: number): NodeData {

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
	public addNodeWithModel(newTopologyNode: TopologyNode): NodeData {

		var newNode = new NodeData(newTopologyNode);
		var collision = true;
		var id = this.nodes.length + 1;
		while (collision) {

			collision = false;
			for (var dataNode of this.nodes) {

				if (dataNode.id == newNode.id) {

					id++;
					newNode.id = 'node' + id;
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
		return newNode;
	}

	/**
	 * Remove a node.
	 * 
	 * @param id identifier of the node to remove.
	 * 
	 * @return true if the node is removed.
	 */
	public removeNode(id: string): boolean {

		for (let i = 0; i < this.nodes.length; i++) {

			if (id == this.nodes[i].id) {

				this.nodes.splice(i, 1);
				return true;
			}
		}
		return false;
	}

	/**
	 * Update the position of a node.
	 * 
	 * @param id identifier of the node.
	 * @param point to teh new node position.
	 * 
	 * @return true if the node is updated. 
	 */
	public updateNodePosition(id: string, point: IPoint): NodeData | null {

		for (var node of this.nodes) {

			if (id == node.id) {

				node.position = point;
				return node;
			}
		}

		return null;
	}

	/**
	 * Return the topology model associated to the view.
	 */
	public get model(): Topology {

		var model = new Topology();
		model.id = this.min.id;
		model.name = this.min.name;
		model.description = this.min.description;
		model.nodes = [];
		model.connections = [];
		for (var node of this.nodes) {

			model.nodes.push(node.model);

		}

		for (var connection of this.connections) {

			model.connections.push(connection.model);

		}

		return model;

	}

	/**
	 * Update the model of a node.
	 * 
	 * @param id identifier of the node.
	 * @param model for the node.
	 * 
	 * @return true if the node is updated. 
	 */
	public updateNodeModel(id: string, model: TopologyNode): boolean {

		for (var node of this.nodes) {

			if (node.id == id) {

				node.model = model;
				return true;
			}
		}

		return false;
	}

	/**
	 * Update the model of a connection.
	 * 
	 * @param id identifier of the connection.
	 * @param model for the connection.
	 * 
	 * @return true if the connection is updated. 
	 */
	public updateConnectionModel(id: string, model: DesignTopologyConnection): boolean {

		for (var connection of this.connections) {

			if (connection.id == id) {

				connection.model = model;
				return true;
			}
		}

		return false;
	}


	/**
	 * Add a connection to the topology.
	 */
	public addConnectionWithModel(newTopologyConnection: DesignTopologyConnection): ConnectionData {

		var newConnection = new ConnectionData(newTopologyConnection);
		var collision = true;
		var id = this.connections.length + 1;
		while (collision) {

			collision = false;
			for (var dataConnection of this.connections) {

				if (dataConnection.id == newConnection.id) {

					id++;
					newConnection.id = 'connection' + id;
					collision = true;
					break;

				}
			}

		}
		this.connections.push(newConnection);
		return newConnection;
	}

}