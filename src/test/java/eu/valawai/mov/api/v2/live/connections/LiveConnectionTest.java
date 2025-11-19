/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.connections;

import java.util.ArrayList;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;

/**
 * Test the {@link LiveConnection}.
 *
 * @see LiveConnection
 *
 * @author VALAWAI
 */
public class LiveConnectionTest extends ModelTestCase<LiveConnection> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LiveConnection createEmptyModel() {

		return new LiveConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(LiveConnection model) {

		model.id = ValueGenerator.nextObjectId();
		model.enabled = ValueGenerator.flipCoin();
		model.createTimestamp = ValueGenerator.nextPastTime();
		model.updateTimestamp = ValueGenerator.nextPastTime();
		final var endPointBuilder = new LiveEndPointTest();
		model.source = endPointBuilder.nextModel();
		model.target = endPointBuilder.nextModel();

		final var max = ValueGenerator.rnd().nextInt(0, 4);
		if (max > 0) {

			final var notificationBuilder = new LiveNotificationTest();
			model.notifications = new ArrayList<>();
			for (var i = 0; i < max; i++) {

				final var notification = notificationBuilder.nextModel();
				model.notifications.add(notification);
			}
		}
	}

	/**
	 * REturn the live connection associated to an entity.
	 *
	 * @param entity to get the data from.
	 *
	 * @return the live connection associated to the entity.
	 */
	public static LiveConnection from(TopologyConnectionEntity entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new LiveConnection();
			model.id = entity.id;
			model.enabled = entity.enabled;
			model.createTimestamp = entity.createTimestamp;
			model.updateTimestamp = entity.updateTimestamp;
			model.source = LiveEndPointTest.from(entity.source);
			model.target = LiveEndPointTest.from(entity.target);
			if (entity.notifications != null) {

				model.notifications = new ArrayList<>();
				for (final var notificationEntity : entity.notifications) {

					final var notification = LiveNotificationTest.from(notificationEntity);
					model.notifications.add(notification);
				}
			}

			return model;
		}
	}

}
