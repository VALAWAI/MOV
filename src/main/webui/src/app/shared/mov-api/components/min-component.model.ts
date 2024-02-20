/*
  Copyright 2022-2026 VALAWAY

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


}
