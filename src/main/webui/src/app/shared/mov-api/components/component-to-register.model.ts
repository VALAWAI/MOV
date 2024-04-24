/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ComponentType } from "./component-type.model";

/**
 * The information of a component to register.
 *
 * @author VALAWAI
 */
export class ComponentToRegister {

	/**
	 * The type of the component to register.
	 */
	public type: ComponentType | null = null;

	/**
	 * The name of the component to register.
	 */
	public name: string | null = null;

	/**
	 * The version of the component.
	 */
	public version: string | null = null;

	/**
	 * The asyncapi specification in yaml.
	 */
	public asyncapiYaml: string | null = null;


}
