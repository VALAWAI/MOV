/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { IPoint } from "@foblex/2d";
import { ChangeNodeAction } from "./change-node.action";
import { EditorTopologyService } from "../editor-topology.service";
import { EditorNode } from "../editor-node.model";

/**
 * An actin that change teh component of a node..
 */
export class ChangeNodeComponentAction extends ChangeNodeAction {


	/**
	 * The previopus position.
	 */
	private oldPosition: IPoint;

	/**
	 * Create the event with the node that changed.
	 */
	constructor(private node: EditorNode, public newPosition: IPoint) {

		super();
		this.oldPosition = node.position;
	}

	/**
	 * Return teh identifier of the node that has change the position.
	 */
	public override get nodeId(): string {

		return this.node.id;
	}


	/**
	 * Set the new position.
	 */
	public override redo(service: EditorTopologyService): void {

		this.node.position = this.newPosition;

	}

	/**
	 * Restore the old position.
	 */
	public override undo(service: EditorTopologyService): void {

		this.node.position = this.oldPosition;

	}

}

