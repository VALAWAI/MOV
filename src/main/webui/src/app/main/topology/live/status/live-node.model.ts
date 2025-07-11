/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { IPoint } from "@foblex/2d";
import { ComponentType, MinComponent } from '@app/shared/mov-api';

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
	 * Create the node.
	 */
	constructor(
		public component: MinComponent
	) {

	}

	/**
	 * 
	*/
	public updatePosition(x: number, y: number): void {
		throw new Error("Method not implemented.");
	}

	/**
	 * The identifier of the node.
	 */
	public get id(): string {

		return this.component.id || 'node_0';
	}


	/**
	 * The name of the node.
	 */
	public get name(): string {

		return this.component.name || '';
	}

	/**
	 * The type of the node.
	 */
	public get type(): ComponentType {

		return this.component.type || 'C0';
	}

}
