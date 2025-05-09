/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ComponentType } from "./component-type.model";

/**
 * A component that is in VALAWAI.
 *
 * @author VALAWAI
 */
export class MinComponent {

	/**
	 * The identifier of the component.
	 */
	public id: string | null = null;

	/**
	 * The name of the component.
	 */
	public name: string | null = null;

	/**
	 * The description of the component.
	 */
	public description: string | null = null;

	/**
	 * The type of the component.
	 */
	public type: ComponentType | null = null;


	/**
	 * Check if this component record is equals to another.
	 */
	public static equals(source: MinComponent | null | undefined, target: MinComponent | null | undefined): boolean {

		return source != null
			&& target != null
			&& source.id === target.id
			&& source.name === target.name
			&& source.description === target.description
			&& source.type === target.type;
	}
}
