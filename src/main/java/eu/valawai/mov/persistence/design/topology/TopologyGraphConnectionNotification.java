/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.topology;

import eu.valawai.mov.api.Model;

/**
 * Represents the component that needs to be notified whenever a message passes
 * through a specific connection.
 *
 * @author VALAWAI
 */
public class TopologyGraphConnectionNotification extends Model {

	/**
	 * The unique identifier (tag) of the recipient or topic to which this
	 * notification is directed. This ensures the notification is routed to the
	 * correct destination.
	 */
	public String targetTag;

	/**
	 * The specific channel or endpoint on the target where this notification should
	 * be delivered. This defines how the recipient will receive and potentially
	 * process the incoming notification.
	 */
	public String targetChannel;

	/**
	 * An optional code snippet or reference used to transform the notification's
	 * content from its original format to the expected format for the
	 * {@code targetChannel}. If no transformation is needed (e.g., the content is
	 * already in the correct format), this field may be {@code null} or empty.
	 */
	public String convertCode;

	/**
	 * The visual type of the connection. Thus how the connection is represented in
	 * the UI.
	 */
	public TopologyGraphConnectionType type;

}
