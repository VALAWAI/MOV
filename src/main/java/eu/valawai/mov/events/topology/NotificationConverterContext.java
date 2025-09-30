/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionNotification;

/**
 * The context used in the {@link MessageConverter} scripts.
 *
 * @see MessageConverter
 *
 * @author VALAWAI
 */
public class NotificationConverterContext extends MessageConverterContext {

	/**
	 * The notification to send the message.
	 */
	protected final TopologyConnectionNotification notification;

	/**
	 * Create a context for a notification.
	 *
	 * @param entity       with the connection information for the context.
	 * @param notification to send the message.
	 */
	public NotificationConverterContext(TopologyConnectionEntity entity, TopologyConnectionNotification notification) {

		super(entity);
		this.notification = notification;
	}

	/**
	 * Return the identifier of the notification target node.
	 *
	 * @return the notification target node identifier.
	 */
	public String notificationTargetId() {

		return this.notification.node.componentId.toHexString();
	}

	/**
	 * Return the type of the notification target node.
	 *
	 * @return the notification target node type.
	 */
	public String notificationTargetType() {

		return this.notification.node.inferComponentType().name();
	}

	/**
	 * Return the name of the notification target node.
	 *
	 * @return the notification target node name.
	 */
	public String notificationTargetName() {

		return this.notification.node.inferComponentName();
	}

}
