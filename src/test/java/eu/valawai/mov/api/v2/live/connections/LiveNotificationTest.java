/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.connections;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionNotification;

/**
 * Test the {@link LiveNotification}.
 *
 * @see LiveNotification
 *
 * @author VALAWAI
 */
public class LiveNotificationTest extends ModelTestCase<LiveNotification> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LiveNotification createEmptyModel() {

		return new LiveNotification();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(LiveNotification model) {

		model.node = new LiveEndPointTest().nextModel();
		model.enabled = ValueGenerator.flipCoin();
		model.convertCode = ValueGenerator.nextEchoConvertJSCode();
	}

	/**
	 * REturn the live notification define in an entity.
	 *
	 * @param entity to get the data from.
	 *
	 *
	 * @return live notification with the data of then entity.
	 */
	public static LiveNotification from(TopologyConnectionNotification entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new LiveNotification();
			model.node = LiveEndPointTest.from(entity.node);
			model.enabled = entity.enabled;
			model.convertCode = entity.notificationMessageConverterJSCode;
			return model;
		}
	}

}
