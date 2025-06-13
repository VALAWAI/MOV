/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { ComponentType } from "@app/shared/mov-api";
import { IPoint } from "@foblex/2d";

/**
 * A model that contains the information of a node that is visible in the topology editor.
 *
 * @author VALAWAI
 */
export class TopologyViewNodeModel {
	
	/**
	 * The identifier of the node.
	 */
	public id: string = "0";

	/**
	 * The timestamp when the log has added.
	 */
	public position: IPoint = { x: 0, y: 0 };

	/**
	 * The type of the component.
	 */
	public type: ComponentType = "C0";


}
