/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorNode } from "../editor-node.model";
import { TopologyEditorService } from "../topology.service";
import { ChangeNodeAction } from "./change-node.action";

/**
 * An action where a node is involved.
 */
export abstract class AbstractNodeAction implements ChangeNodeAction {


	/**
	 * Create an action over a node. 
	 */
	constructor(public node: EditorNode) {

	}

	/**
	 * Return the id of the conneciton to add.
	 */
	public get nodeId(): string {

		return this.node.id;
	}

	/**
	 * Undo the action.
	 */
	public abstract undo(service: TopologyEditorService): void;

	/**
	 * Do the action. 
	 */
	public abstract redo(service: TopologyEditorService): void;

}