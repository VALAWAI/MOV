/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import {
	LiveTopologyComponent,
	LiveTopologyComponentOutConnection,
	ComponentType
} from "@app/shared/mov-api";
import { IPoint } from "@foblex/2d";
import { StatusEndpoint } from "./status-endpoint.model";


/**
 * A node in the status graph.
 */
export class StatusNode {

	/**
	 * The position of the node.
	 */
	public position: IPoint = { x: 0, y: 0 };

	/**
	 * The width of the node. 
	 */
	public width: number = 32;

	/**
	 * The height of the node.
	 */
	public height: number = 32;

	/**
	 * The name of the component.
	 */
	public name: string | null = null;

	/**
	 * The type of the component.
	 */
	public type: ComponentType | null = null;

	/**
	 * The endpoint of the node.
	 */
	public endpoints: StatusEndpoint[] = [];

	/**
	 * Create the node.
	 */
	constructor(public model: string | LiveTopologyComponent | LiveTopologyComponentOutConnection) { }

	/**
	 * Get the node identifier.
	 */
	public get id(): string {

		if (typeof this.model === 'string') {

			return this.model;

		} else {

			return this.model.id!;
		}

	}

	/**
	 * Check if 
	*/
	public equalId(id: string | LiveTopologyComponent | LiveTopologyComponentOutConnection): boolean {

		if (typeof id === 'string') {

			return this.id == id;

		} else {

			return this.id! == id.id;
		}
	}

	/**
	 * Create a new node for a compoennt.
	 */
	public updateWith(model: LiveTopologyComponent) {

		this.model = model;
		this.type = model.type;
		this.name = model.name;
		if (this.name != null && this.name.match(/c[0|1|2]_.+/i) != null) {

			this.name = this.name.substring(3);
		}
	}


	/**
	 * Return the endpoint for the specified channel.
	 */
	public searchOrCreateEndpointFor(channel: string | null, isSource: boolean): StatusEndpoint {

		var endpoint = this.endpoints.find(e => e.isSource == isSource && e.channel === channel) || null;
		if (endpoint == null) {

			endpoint = new StatusEndpoint(this.id, channel, isSource);
			this.endpoints.push(endpoint);
		}

		return endpoint;

	}

	/**
	 * Obtain the model of a connection defined in the node.
	 */
	public searchLiveTopologyComponentOutConnectionWith(id: string): LiveTopologyComponentOutConnection | null {

		if (typeof this.model === 'object' && 'connections' in this.model) {

			return this.model.connections!.find(c => c.id == id) || null;
		}

		return null;
	}

	/**
	 * Return the endpoint for the specified channel.
	 */
	public searchEndpointByChannel(channel: string): StatusEndpoint | null {

		return this.endpoints.find(e => e.channel === channel) || null;

	}


}
