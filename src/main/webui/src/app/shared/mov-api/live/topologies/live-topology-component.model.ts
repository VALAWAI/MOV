/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { MinComponent } from "../../components/min-component.model";
import { LiveTopologyComponentOutConnection } from "./live-topology-component-out-connection.model";


/**
 * A node that form part of a live topology.
 *
 * @author VALAWAI
 */
export class LiveTopologyComponent extends MinComponent{

	/**
	 * The connections that exit from this node.
	 */
	public connections: LiveTopologyComponentOutConnection[] | null = null;

}
