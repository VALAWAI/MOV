/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import {PayloadSchema} from './payload-schema.model';

/**
 * The definition of a channel that is defined on a component.
 *
 * @author VALAWAI
 */
export class ChannelSchema {

	/**
	 * The identifier of the channel.
	 */
	public name: string | null = null;

	/**
	 * The description of the channel.
	 */
	public description: string | null = null;

	/**
	 * The type of payload that the channel can receive.
	 */
	public subscribe: PayloadSchema | null = null;

	/**
	 * The type of payload that the channel can send.
	 */
	public publish: PayloadSchema | null = null;
}
