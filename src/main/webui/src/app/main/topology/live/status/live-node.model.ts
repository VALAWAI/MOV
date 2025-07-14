/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { IPoint } from "@foblex/2d";
import { ComponentType, MinComponent, MinConnectionPage } from '@app/shared/mov-api';
import { LiveEndpoint } from "./live-endpoint.model";

/**
 * A node in the live topology.
 */
export class LiveNode {

	/**
	 * The position of the node.
	 */
	public position: IPoint = { x: 0, y: 0 };

	/**
	 * The width of the node. 
	 */
	public width: number = 100;

	/**
	 * The height of the node.
	 */
	public height: number = 100;

	/**
	 * The endpoint where teh connection depart or arrive.
	 */
	public endpoints: LiveEndpoint[] = [];

	/**
	 * Create the node.
	 */
	constructor(
		public component: MinComponent
	) {

	}


	/**
	 * The identifier of the node.
	 */
	public get id(): string {

		return this.component.id || 'node_0';
	}

	/**
	 * Check if the name has the type at the beginning.
	 */
	private nameStartWithType(): boolean {

		return this.component.name != null && this.component.name.match(/c[0|1|2]_.+/i) != null;

	}


	/**
	 * The name of the node.
	 */
	public get name(): string {

		if (this.nameStartWithType()) {

			return this.component!.name!.substring(3);

		} else {

			return '';
		}
	}

	/**
	 * The type of the node.
	 */
	public get type(): ComponentType {

		if (this.component.type) {

			return this.component.type;

		} else if (this.nameStartWithType()) {

			return this.component!.name!.substring(0, 2).toUpperCase() as ComponentType;

		} else {

			return 'C0';
		}
	}

	/**
	 * Check if the channle is defined in this node.
	 */
	public isNodeChannel(channel: string | null | undefined): boolean {

		if (channel) {

			return channel.match(new RegExp(".+" + this.type + ".+" + this.name + ".+", "i")) != null;

		} else {

			return false;
		}
	}

	/**
	 * Update the endpoint of the node to match the active connections.
	 */
	public updateEndpointsWith(page: MinConnectionPage): boolean {

		var changed: boolean = false;
		if (page.connections != null && page.connections.length > 0) {

			PAGE: for (var connection of page.connections) {

				for (var endpoint of this.endpoints) {

					if (connection.source == endpoint.id || connection.target == endpoint.id) {

						continue PAGE;
					}

				}

				if (this.isNodeChannel(connection.source)) {

					var endpoint = new LiveEndpoint();
					endpoint.id = connection.source!;
					endpoint.isSource = true;
					this.endpoints.push(endpoint);
					changed = true;
				}

				if (this.isNodeChannel(connection.target)) {

					var endpoint = new LiveEndpoint();
					endpoint.id = connection.target!;
					endpoint.isSource = false;
					this.endpoints.push(endpoint);
					changed = true;
				}

			}

			ENDPOINT: for (var i = 0; i < this.endpoints.length; i++) {

				var endpointId = this.endpoints[i].id;
				for (var connection of page.connections) {

					if (connection.source == endpointId || connection.target == endpointId) {

						continue ENDPOINT;
					}
				}

				this.endpoints.splice(i, 1);
				i--;
				changed = true;
			}


		} else if (this.endpoints.length > 0) {

			this.endpoints.splice(0, this.endpoints.length);
			changed = true;
		}


		return changed;
	}

}
