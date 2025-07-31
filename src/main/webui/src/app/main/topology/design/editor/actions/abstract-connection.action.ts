/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { EditorConnection } from "../editor-connection.model";
import { TopologyEditorService } from "../topology.service";
import { ChangeConnectionAction } from "./change-connection.action";

/**
 * The default implementation of an action over a connection.
 */
export abstract class AbstractConnectionAction implements ChangeConnectionAction {


	/**
	 * Create an action over a connection. 
	 */
	constructor(public connection: EditorConnection) {

	}

	/**
	 * Return the id of the conneciton to add.
	 */
	public get connectionId(): string {

		return this.connection.id;
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