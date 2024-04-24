/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ChannelSchema } from "./channel-schema.model";
import { MinComponent } from "./min-component.model";

/**
 * A component that is in VALAWAI.
 *
 * @author VALAWAI
 */
export class Component extends MinComponent {

	/**
	 * The version of the component.
	 */
	public version: string | null = null;

	/**
	 * The API version of the component.
	 */
	public apiVersion: string | null = null;

	/**
	 * The time, epoch in seconds, when the component is registered.
	 */
	public since: number | null = null;

	/**
	 * The channels defined on the component.
	 */
	public channels: ChannelSchema[] | null = null;

}
