/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.topologies;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionNotification;
import eu.valawai.mov.persistence.live.topology.TopologyNode;

/**
 * Test the {@link LiveTopologyConnectionEndpoint}.
 *
 * @see LiveTopologyConnectionEndpoint
 *
 * @author VALAWAI
 */
public class LiveTopologyConnectionEndpointTest extends ModelTestCase<LiveTopologyConnectionEndpoint> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LiveTopologyConnectionEndpoint createEmptyModel() {

		return new LiveTopologyConnectionEndpoint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(LiveTopologyConnectionEndpoint model) {

		model.id = ValueGenerator.nextObjectId();
		model.channel = ValueGenerator.nextPattern("valawai/C0/dummy/data/model_{0}");
		model.enabled = ValueGenerator.flipCoin();
	}

	/**
	 * Convert a model from a node.
	 *
	 * @param node to get the data.
	 *
	 * @return the model with the data of the node.
	 */
	public static LiveTopologyConnectionEndpoint from(TopologyNode node) {

		if (node == null) {

			return null;

		} else {

			final var model = new LiveTopologyConnectionEndpoint();
			model.id = node.componentId;
			model.channel = node.channelName;
			return model;

		}

	}

	/**
	 * Convert a model from a notification.
	 *
	 * @param notification to get the data.
	 *
	 * @return the model with the data of the notification.
	 */
	public static LiveTopologyConnectionEndpoint from(TopologyConnectionNotification notification) {

		if (notification == null) {

			return null;

		} else {

			final var model = new LiveTopologyConnectionEndpoint();
			if (notification.node != null) {

				model.id = notification.node.componentId;
				model.channel = notification.node.channelName;
			}
			model.enabled = notification.enabled;
			return model;

		}

	}

}
