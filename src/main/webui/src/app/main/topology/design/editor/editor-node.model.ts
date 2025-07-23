/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ComponentType, TopologyNode, DesignTopologyConnection } from "@app/shared/mov-api";
import { IPoint } from "@foblex/2d";
import { EditorEndpoint } from './editor-endpoint.model';
import { EditorConnection } from "./editor-connection.model";

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
	constructor(public model: TopologyNode | DesignTopologyConnection) {


	}

	/**
	 * Return the identifier of the node.
	 */
	public get id(): string {

		if ('tag' in this.model) {

			return this.model.tag!;

		} else {

			return this.model.source?.nodeTag + '->notification->' + this.model.source?.nodeTag;

		}
	}


	/**
	 * Chck if the node has a component.
	 */
	public get isTopologyNode(): boolean {

		return ('position' in this.model && this.model.position != null);
	}


	/**
	 * Return the position of the node.
	 */
	public get position(): IPoint {

		if ('position' in this.model) {

			return this.model.position;

		} else {

			return this.model.notificationPosition!;
		}

	}

	/**
	 * Change the  position of the node.
	 */
	public set position(point: IPoint) {

		if ('position' in this.model) {

			this.model.position = point;

		} else {

			this.model.notificationPosition! = point;
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

		if ('position' in this.model) {

			return (this.model as TopologyNode).component!.name;

		} else {

			return null;
		}
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
	 * Check if this is a notificaiton node of the specified connection.
	 */
	public isNotificationNodeOf(connection: EditorConnection): boolean {

		if (!this.isTopologyNode) {

			var defined = this.model as DesignTopologyConnection;
			return JSON.stringify(defined.source) === JSON.stringify(connection.model.source)
				&& JSON.stringify(defined.target) === JSON.stringify(connection.model.target);

		}
		return false;
	}

} 