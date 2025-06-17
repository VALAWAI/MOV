/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ChannelSchema } from "../../components/channel-schema.model";
import { ComponentType } from "../../components/component-type.model";
import { VersionInfo } from './version.info.model';


/**
 * The definition of a component that can be used in a topology.
 *
 * @author VALAWAI
 */
export class ComponentDefinition {

	/**
	 * The identifier of the component.
	 */
	public id: string | null = null;

	/**
	 * The type of the component.
	 */
	public type: ComponentType | null = null;

	/**
	 * The name of the component.
	 */
	public name: string | null = null;

	/**
	 * The description of the component.
	 */
	public description: string | null = null;

	/**
	 * The documentation link of the component.
	 */
	public docsLink: string | null = null;

	/**
	 * The git link of the component.
	 */
	public gitHubLink: string | null = null;

	/**
	 * The version of the component.
	 */
	public version: VersionInfo | null = null;

	/**
	 * The version of the API of the component.
	 */
	public apiVersion: VersionInfo | null = null;

	/**
	 * The channels defined on the component.
	 */
	public channels: ChannelSchema[] | null = null;
}
