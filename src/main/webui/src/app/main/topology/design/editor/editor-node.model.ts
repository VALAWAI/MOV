/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ComponentDefinition } from "@app/shared/mov-api";
import { IPoint } from "@foblex/2d";
import { EditorEndpoint } from './editor-endpoint.model';

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
	 * Get a endpoint or create it if not exist.
	 */
	public searchEndpoint(channel: string | null, isSource: boolean): EditorEndpoint | null {

		return this.endpoints.find(e => e.channel === channel && e.isSource === isSource) || null;
	}

	/**
	 * Get a endpoint or create it if not exist.
	 */
	public searchEndpointOrCreate(channel: string | null, isSource: boolean): EditorEndpoint {

		var endpoint = this.searchEndpoint(channel, isSource);
		if (endpoint == null) {

			endpoint = new EditorEndpoint(this.id, channel, isSource);
			this.endpoints.push(endpoint);
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

} 