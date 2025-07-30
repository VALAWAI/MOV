/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorNode } from "../editor-node.model";
import { EditorTopologyService } from "../editor-topology.service";
import { ChangeNodeAction } from "./change-node.action";

/**
 * An actin to remove a node.
 */
export class RemoveNodeAction extends ChangeNodeAction {

	/**
	 * The conneciton tha has been removed.
	 */
	private node: EditorNode | null = null;

	/**
	 * Create the action with the node to be removed.
	 */
	constructor(public override nodeId: string) {

		super();
	}

	/**
	 * Undo the remove.
	 */
	public override undo(service: EditorTopologyService): void {

		service.nodes.push(this.node!);

	}

	/**
	 * Remove the node. 
	 */
	public override redo(service: EditorTopologyService): void {

		var index = service.nodes.findIndex(c => c.id == this.nodeId);
		this.node = service.nodes.splice(index, 1)[0];

	}


}

