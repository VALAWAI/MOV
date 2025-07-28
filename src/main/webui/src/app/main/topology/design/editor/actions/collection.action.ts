/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorTopologyService, TopologyChangeAction } from "../editor-topology.service";

/**
 * A set of collection to do at the same time.
 */
export class CollectionAction extends TopologyChangeAction {

	/**
	 * specify a set of action todo.
	 */
	constructor(private actions: TopologyChangeAction[]) {

		super();
	}

	/**
	 * Redo all teh actions.
	 */
	public override redo(service: EditorTopologyService): void {

		for (var action of this.actions) {

			action.redo(service);

		}

	}

	/**
	 * Undo all teh actions.
	 */
	public override undo(service: EditorTopologyService): void {

		for (var action of this.actions) {

			action.undo(service);

		}

	}

}

