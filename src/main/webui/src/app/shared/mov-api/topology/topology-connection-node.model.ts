/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { MinComponent } from '../components/min-component.model';
import { ChannelSchema } from '../components/channel-schema.model';

/**
 * A topology connection that is in VALAWAI.
 *
 * @author VALAWAI
 */
export class TopologyConnectionNode {

	/**
	 * The component that is the node of the connection.
	 */
	public component: MinComponent | null = null;

	/**
	 * The channel thought the events are sent/received in the connection.
	 */
	public channel: ChannelSchema | null = null;

}
