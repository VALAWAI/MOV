/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ComponentType, TopologyNode } from "@app/shared/mov-api";
import { IPoint } from "@foblex/2d";
import { EditorEndpoint } from './editor-endpoint.model';

/**
 * A node in the graph od the EditorTopology..
 */
export class EditorNode {


	/**
	 * The width of the node. 
	 */
	public width: number = 32;

	/**
	 * The height of the node.
	 */
	public height: number = 32;

	/**
	 * The endpoint of the node.
	 */
	public endpoints: EditorEndpoint[] = [];

	/**
	 * Create a new model.
	 */
	constructor(public model: TopologyNode) {


	}

	/**
	 * Return the identifier of the node.
	 */
	public get id(): string {

		if (this.hasComponent) {

			return this.model.tag!;

		}

		return 'node_0';

	}


	/**
	 * Chck if the node has a component.
	 */
	public get hasComponent(): boolean {

		return ('component' in this.model && this.model.position != null);
	}


	/**
	 * Return the position of the node.
	 */
	public get position(): IPoint {

		if (this.hasComponent) {

			return this.model.position;

		}
		return { x: 0, y: 0 };
	}

	/**
	 * Change the  position of the node.
	 */
	public set position(point: IPoint) {

		if (this.hasComponent) {

			this.model.position = point;

		}

	}



	/**
	 * Return the type of component associated to the node.
	 */
	public get type(): ComponentType {

		return (this.model as TopologyNode).component!.type!;
	}

	/**
	 * Return the name for the node.
	 */
	public get name(): string | null {

		return (this.model as TopologyNode).component!.name;
	}

	/**
	 * Check if the component of the node has channels.
	 */
	public get hasChannels(): boolean {

		return 'component' in this.model && this.model.component != null
			&& this.model.component.channels != null && this.model.component.channels.length > 0;
	}

	/**
	 * Get a endpoint or create it if not exist.
	 */
	public searchEndpointOrCreate(channel: string, isSource: boolean): EditorEndpoint {

		var endpoint = this.endpoints.find(e => e.channel == channel && e.isSource == isSource);
		if (endpoint == null) {

			endpoint = new EditorEndpoint(this.id, channel, isSource);
			this.endpoints.push(endpoint);
		}

		return endpoint;

	}

} 