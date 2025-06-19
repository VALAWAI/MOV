/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { MinTopology } from "./min-topology.model";

/**
 * A page of the {@link MinTopology} that satisfy a query.
 *
 * @author VALAWAI
 */
export class MinTopologyPage {

	/**
	 * The number of components that satisfy the query.
	 */
	public total: number = 0;

	/**
	 * The offset of the first returned component.
	 */
	public offset: number = 0;

	/**
	 * The components that match the query.
	 */
	public topologies: MinTopology[] | null = null;


}
