/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

/**
 * The event that notify that an element of the topology has changed.
 */
export interface TopologyEditorChangedEvent {

	/**
	 * The type of actin that has changed the topology.
	 */
	type: 'CHANGED_TOPOLOGY'
	| 'ADDED_NODE'
	| 'REMOVED_NODE'
	| 'CHANGED_NODE'
	| 'ADDED_CONNECTION'
	| 'REMOVED_CONNECTION'
	| 'CHANGED_CONNECTION'
	;

	/**
	 * The identifier of the changed event.
	 */
	id: string | null;
}
