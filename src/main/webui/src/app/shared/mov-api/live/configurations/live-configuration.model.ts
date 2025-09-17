/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyBehavior } from "./topology-behaviour.model";


/**
 * The configuration of the MOV instance.
 *
 * @author VALAWAI
 */
export class LiveConfiguration {

	/**
	 * The identifier of the designed topology to follow in live.
	 */
	public topologyId: string | null = null;

	/**
	 * The behaviour to follow when a component is registered in the MOV.
	 */
	public registerComponent: TopologyBehavior | null = null;

	/**
	 * The behaviour to follow when a component is registered in the MOV.
	 */
	public createConnection: TopologyBehavior | null = null;


}
