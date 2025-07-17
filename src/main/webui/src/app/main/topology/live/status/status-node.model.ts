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
	constructor(public id: string) { }

	/**
	 * Create a new node for a compoennt.
	 */
	public static createComponentNode(position: number, model: LiveTopologyComponent): StatusNode {


		var node = new StatusNode(model.id!);
		node.position = { x: 200 * position, y: 200 * position };
		node.type = model.type;
		node.name = model.name;
		if (node.name != null && node.name.match(/c[0|1|2]_.+/i) != null) {
			arguments

			node.name = node.name!.substring(3);
		}

		return node;
	}

	/**
	 * Create a new node used to send the notifications.
	 */
	public static createNotificationNode(position: number, model: LiveTopologyComponentOutConnection): StatusNode {


		var node = new StatusNode(model.id!);
		node.position = { x: 200 * position, y: 200 * position };
		var target = new StatusEndpoint(node.id, null, false);
		node.endpoints.push(target);
		var source = new StatusEndpoint(node.id, null, true);
		node.endpoints.push(source);
		return node;
	}


}
