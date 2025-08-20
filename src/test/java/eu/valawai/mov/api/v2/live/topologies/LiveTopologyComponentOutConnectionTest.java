/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.topologies;

import java.util.ArrayList;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;

/**
 * Test the {@link LiveTopologyComponentOutConnection}.
 *
 * @see LiveTopologyComponentOutConnection
 *
 * @author VALAWAI
 */
public class LiveTopologyComponentOutConnectionTest extends ModelTestCase<LiveTopologyComponentOutConnection> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LiveTopologyComponentOutConnection createEmptyModel() {

		return new LiveTopologyComponentOutConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(LiveTopologyComponentOutConnection model) {

		model.id = ValueGenerator.nextObjectId();
		model.channel = ValueGenerator.nextPattern("valawai/C0/dummy/data/model_{0}");
		final var builder = new LiveTopologyConnectionEndpointTest();
		model.target = builder.nextModel();
		final var max = ValueGenerator.rnd().nextInt(0, 10);
		if (max > 0) {

			model.notifications = new ArrayList<>();
			for (var i = 0; i < max; i++) {

				final var endpoint = builder.nextModel();
				model.notifications.add(endpoint);
			}
		}

	}

	/**
	 * Return the model with the data of an entity.
	 *
	 * @param entity to get the data.
	 *
	 * @return the model with the data of the model.
	 */
	public static LiveTopologyComponentOutConnection from(TopologyConnectionEntity entity) {

		if (entity == null) {
			return null;

		} else {

			final var model = new LiveTopologyComponentOutConnection();
			model.id = entity.id;
			if (entity.source != null) {

				model.channel = entity.source.channelName;
			}
			model.target = LiveTopologyConnectionEndpointTest.from(entity.target);
			if (model.target != null) {

				model.target.enabled = entity.enabled;
			}
			if (entity.notifications != null && !entity.notifications.isEmpty()) {

				model.notifications = new ArrayList<>();
				for (final var defNotification : entity.notifications) {

					final var notification = LiveTopologyConnectionEndpointTest.from(defNotification);
					model.notifications.add(notification);
				}
				model.notifications.sort((c1, c2) -> {
					var cmp = c1.id.compareTo(c2.id);
					if (cmp == 0) {

						cmp = c1.channel.compareTo(c2.channel);
					}
					return cmp;
				});

			}

			return model;
		}
	}

}
