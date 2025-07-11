/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { IPoint } from "@foblex/2d";
import { ComponentType, MinComponent } from '@app/shared/mov-api';

/**
 * A connection in the live topology.
 */
export class LiveConnection {

	/**
	 * Create the connection.
	 */
	constructor(
		public component: MinComponent
	) {

	}

	/**
	 * The idetifier of the source.
	 */
	get sourceId(): string {
		throw new Error("Method not implemented.");
	}

	/**
	 * The idetifier of the target.
	 */
	get targetId(): string {
		throw new Error("Method not implemented.");
	}

	/**
	 * The identifier of the connection.
	 */
	public get id(): string {

		return this.component.id || 'connection_0';
	}

	/**
	 * The position of the connection.
	 */
	public position: IPoint = { x: 0, y: 0 };

	/**
	 * The name of the connection.
	 */
	public get name(): string {

		return this.component.name || '';
	}

	/**
	 * The type of the connection.
	 */
	public get type(): ComponentType {

		return this.component.type || 'C0';
	}

}
