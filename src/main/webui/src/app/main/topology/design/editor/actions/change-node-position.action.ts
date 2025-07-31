/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { IPoint } from "@foblex/2d";
import { ChangeNodeAction } from "./change-node.action";
import { TopologyEditorService } from "../topology.service";

/**
 * An actin that change the position of a node.
 */
export class ChangeNodePositionAction implements ChangeNodeAction {


	/**
	 * The previopus position.
	 */
	private oldPosition: IPoint = { x: 0, y: 0 };

	/**
	 * Create the event with the node that changed.
	 */
	constructor(public nodeId: string, public newPosition: IPoint) {

	}


	/**
	 * Set the new position.
	 */
	public redo(service: TopologyEditorService): void {

		var node = service.getNodeWith(this.nodeId)!;
		this.oldPosition = node.position;
		node.position = this.newPosition;
		service.notifyChangedNode(this.nodeId);

	}

	/**
	 * Restore the old position.
	 */
	public undo(service: TopologyEditorService): void {

		var node = service.getNodeWith(this.nodeId)!;
		node.position = this.oldPosition;
		service.notifyChangedNode(this.nodeId);
	}

}

