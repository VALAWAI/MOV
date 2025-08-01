/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorNode } from "../editor-node.model";
import { TopologyEditorService } from "../topology.service";
import { AbstractCompositeAction } from "./abstract-composite.action";
import { ChangeNodeAction } from "./change-node.action";
import { RemoveConnectionAction } from "./remove-connection.action";

/**
 * An actin to remove a node.
 */
export class RemoveNodeAction extends AbstractCompositeAction implements ChangeNodeAction {

	/**
	 * The node that has been removed.
	 */
	private node: EditorNode | null = null


	/**
	 * Create the event with the new node to remove.
	 */
	constructor(public nodeId: string) {

		super();
	}


	/**
	 * Undo the remove.
	 */
	public override undo(service: TopologyEditorService): void {

		super.undo(service);
		service.nodes.push(this.node!);
		service.notifyAddedNode(this.nodeId);
	}

	/**
	 * Remove the node. 
	 */
	public override redo(service: TopologyEditorService): void {

		var index = service.nodes.findIndex(c => c.id == this.nodeId);
		this.node = service.nodes.splice(index, 1)[0];
		service.notifyRemovedNode(this.nodeId);
		if (this.actions.length > 0) {

			super.redo(service);

		} else {

			for (var i = 0; i < service.connections.length; i++) {

				var connection = service.connections[i];
				if (connection.source.nodeId == this.nodeId || connection.target.nodeId == this.nodeId) {

					service.connections.splice(i, 1);
					i--;
					this.addAndRedo( new RemoveConnectionAction(connection.id,connection),service);
				
				}
			}
		}
	}

}

