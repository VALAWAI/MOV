/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import eu.valawai.mov.events.topology.NodePayload;

/**
 * Abstract operation over a {2link TopologyConnectionNotification} defined into
 * a {@link TopologyConnectionEntity}.
 *
 * @param <R> result of the operation.
 * @param <O> operation to do.
 *
 * @see TopologyConnectionEntity#notifications
 *
 * @author VALAWAI
 */
public abstract class AbstractNotificationConnectionOperation<R, O extends AbstractNotificationConnectionOperation<R, O>>
		extends AbstractTopologyConnectionOperation<R, O> {

	/**
	 * The target node of the notification.
	 */
	protected TopologyNode node;

	/**
	 * Set the notification target to be used by this operator.
	 *
	 * @param target where the notification has to be sent.
	 *
	 * @return this operator.
	 */
	public O withNode(TopologyNode target) {

		this.node = target;
		return this.operator();
	}

	/**
	 * Set the notification target to be used by this operator.
	 *
	 * @param node where the notification has to be sent.
	 *
	 * @return this operator.
	 */
	public O withNode(NodePayload node) {

		final var target = new TopologyNode();
		target.componentId = node.componentId;
		target.channelName = node.channelName;
		return this.withNode(target);
	}

}
