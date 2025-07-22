/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyConnectionEndpoint } from "./topology-connection-endpoint.model";


/**
 * Represents the component that needs to be notified whenever a message passes
 * through a specific {@link TopologyConnection}.
 *
 * @author VALAWAI
 */
export class TopologyConnectionNotification {

	/**
	 * The **recipient or delivery endpoint** for the notification, specifying the
	 * target's unique identifier (tag) and the specific channel through which it
	 * should be delivered.
	 */
	public target: TopologyConnectionEndpoint | null = null;

	/**
	 * An optional code snippet or identifier used to transform the notification's
	 * content from its original format to the format expected by the
	 * target channel. Can be null or empty if no transformation is required.
	 */
	public convertCode: string | null = null;

	
}
