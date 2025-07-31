/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ChannelSchema, ComponentDefinition } from "@app/shared/mov-api";
import { IPoint } from "@foblex/2d";
import { EditorEndpoint } from './editor-endpoint.model';
import { distance } from "@app/shared/graph";

/**
 * A node in the graph od the EditorTopology..
 */
export class EditorNode {

	/**
	 * The position of the node.
	  */
	public position: IPoint;

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
	 * A reference to the {@link ComponentDefinition} that this node represents.
	 * This defines the functional behavior and capabilities of the node.
	 */
	public component: ComponentDefinition | null = null;

	/**
	 * Create a new model.
	 */
	constructor(public id: string, public sourceNotification: EditorEndpoint | null = null) {

		this.position = {
			x: 0,
			y: 0
		};
	}

	/**
	 * Get the position of an endpoint.
	 */
	public searchEndpointIndex(channel: string | null, isSource: boolean): number {

		return this.endpoints.findIndex(e => e.channel === channel && e.isSource === isSource);
	}

	/**
	 * Get a endpoint or create it if not exist.
	 */
	public searchEndpoint(channel: string | null, isSource: boolean): EditorEndpoint | null {

		var index = this.searchEndpointIndex(channel, isSource);
		if (index < 0) {

			return null;

		} else {

			return this.endpoints[index];
		}
	}

	/**
	 * Get a endpoint or create it if not exist.
	 */
	public searchEndpointOrCreate(channel: string | null, isSource: boolean): EditorEndpoint {

		var endpoint = this.searchEndpoint(channel, isSource);
		if (endpoint == null) {

			endpoint = new EditorEndpoint(this.id, channel, isSource);
			this.endpoints.push(endpoint);
			this.endpoints.sort((e1, e2) => e1.compareTo(e2));
		}

		return endpoint;

	}

	/**
	 * Return the first endppoint that is or not a source.
	 */
	public fisrtEndPointWithIsSource(isSource: boolean): EditorEndpoint {

		return this.endpoints.find(e => e.isSource == isSource)!;

	}

	/**
	 * A name to order the node.
	 */
	public get name(): string {

		if (this.component != null && this.component.name != null) {

			return this.component.name;

		} else if (this.sourceNotification != null) {

			return this.sourceNotification.id;

		} else {

			return '';
		}

	}

	/**
	 * Return the expected order of this endpoint respect another. 
	 */
	public compareTo(other: EditorNode): number {

		return this.name.localeCompare(other.name);

	}

	/**
	 * Return the distance of the node to the specified point.
	 */
	public distanceTo(source: IPoint | EditorNode): number {

		if ('position' in source) {

			return distance(source.position, this.position);

		} else {

			return distance(source, this.position);
		}

	}

	/**
	 * Check if a point is inside the node.
	 */
	public isPointInside(point: IPoint, delta: number = 7): boolean {

		return (
			point.x >= (this.position.x - delta) &&
			point.x <= (this.position.x + this.width + 2 * delta) &&
			point.y >= (this.position.y - delta) &&
			point.y <= (this.position.y + this.height + 2 * delta)
		);
	}

	/**
	 * Return the schema associated to the channel. 
	 */
	public getChannelSchemaFor(channel: string): ChannelSchema | null {

		if (this.component != null && this.component.channels != null) {

			return this.component.channels.find(c => c.name == channel) || null;
		}

		return null;

	}

} 