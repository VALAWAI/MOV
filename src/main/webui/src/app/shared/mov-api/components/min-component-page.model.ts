/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { MinComponent } from "./min-component.model";

/**
 * A page with some components.
 *
 * @author VALAWAI
 */
export class MinComponentPage {

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
	public components: MinComponent[] | null = null;

}
