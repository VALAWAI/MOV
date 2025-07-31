/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyEditorAction } from "../topology.action";
import { TopologyEditorService } from "../topology.service";


/**
 * The default implementation of an action over a connection.
 */
export abstract class AbstractCompositeAction implements TopologyEditorAction {

	/**
	 * The collections of actions to do.
	 */
	protected actions: TopologyEditorAction[] = [];


	/**
	 * Add an action and redo it.
	 */
	protected addAndRedo(action: TopologyEditorAction, service: TopologyEditorService) {

		action.redo(service);
		this.add(action);
	}

	/**
	 * Add an action .
	 */
	public add(action: TopologyEditorAction) {

		this.actions.push(action);
	}

	/**
	 * Redo all the actions.
	 */
	public redo(service: TopologyEditorService): void {

		for (var action of this.actions) {

			action.redo(service);

		}

	}

	/**
	 * Undo all the actions.
	 */
	public undo(service: TopologyEditorService): void {

		// Undo in reverse order
		for (var i = this.actions.length - 1; i >= 0; i--) {

			const action = this.actions[i];
			action.undo(service);
		}

	}

}