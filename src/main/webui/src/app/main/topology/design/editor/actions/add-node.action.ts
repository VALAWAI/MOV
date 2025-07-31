/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { TopologyEditorService } from "../topology.service";
import { AbstractNodeAction } from "./abstract-node.action";

/**
 * An actin to add a node betwen node.
 */
export class AddNodeAction extends AbstractNodeAction {


	/**
	 * Remove the added node.
	 */
	public undo(service: TopologyEditorService): void {

		var index = service.nodes.findIndex(c => c.id == this.node.id);
		service.nodes.splice(index, 1);
		service.notifyRemovedNode(this.nodeId);

	}

	/**
	 * Add the node. 
	 */
	public redo(service: TopologyEditorService): void {

		service.nodes.push(this.node);
		service.notifyAddedNode(this.nodeId);

	}


}

