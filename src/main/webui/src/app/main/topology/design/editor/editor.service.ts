/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Injectable } from "@angular/core";
import { IPoint } from "@foblex/2d";
import { Observable, Subject } from "rxjs";


export class NodePositionEvent {

	/**
	 * Create the event.
	 */
	constructor(
		public nodeId: String,
		public point: IPoint
	) {

	}
}

/**
 * The service to comunicate with the editor.
 */
@Injectable()
export class EditorService {

	/**
	 * The subject that mange the changes on the node.
	 */
	private movedNodeSubject = new Subject<NodePositionEvent>();


	/**
	 * Listen for cnaged in the node.
	 */
	public get movedNode$(): Observable<NodePositionEvent> {

		return this.movedNodeSubject.asObservable();

	};

	/**
	 * Emit a change in the node.
	 */
	public nodePositionChange(nodeId: string, point: IPoint) {

		this.movedNodeSubject.next(new NodePositionEvent(nodeId, point));

	};
}
