/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import eu.valawai.mov.api.Model;

/**
 * A notification that is generated in the topology.
 *
 * @see TopologyConnectionEntity#notifications
 *
 * @author VALAWAI
 */
public class TopologyConnectionNotification extends Model {

	/**
	 * The node to notify.
	 */
	public TopologyNode node;

	/**
	 * This is {@code true} if the notification is enabled.
	 */
	public boolean enabled;

	/**
	 * The javaScript code that will be executed to convert the message that go from
	 * the source to the target to the message that the notification node can
	 * process.
	 */
	public String notificationMessageConverterJSCode;

}
