/*
  Copyright 2022-2026 VALAWAI

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

	/**
	 * Check if this page is equals to another.
	 */
	public static equals(source: MinComponentPage | null | undefined, target: MinComponentPage | null | undefined): boolean {

		if (source == null && target == null) {

			return true;

		} else if (
			source != null
			&& target != null
			&& source.total === target.total
		) {
			if ((source.components == null || source.components.length == 0)
				&& (target.components == null || target.components.length == 0)
			) {

				return true;

			} else if (
				source.components != null
				&& target.components != null
				&& source.components.length === target.components.length
			) {

				for (var i = 0; i < source.components.length; i++) {

					var sourceComponent = source.components[i];
					var targetComponent = target.components[i];
					if (!MinComponent.equals(sourceComponent, targetComponent)) {

						return false;
					}
				}
				return true;
			}
		}

		return false;
	}

}
