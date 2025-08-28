/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.events.PayloadTestCase;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionNotification;

/**
 * Test the {@link NotificationPayload}.
 *
 * @see NotificationPayload
 *
 * @author VALAWAI
 */
public class NotificationPayloadTest extends PayloadTestCase<NotificationPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NotificationPayload createEmptyModel() {

		return new NotificationPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(NotificationPayload model) {

		model.enabled = ValueGenerator.flipCoin();
		model.target = new NodePayloadTest().nextModel();
		model.converterJSCode = ValueGenerator.nextEchoConvertJSCode();
	}

	/**
	 * Return the model from the data of an entity.
	 *
	 * @param entity to get the data of the model.
	 *
	 * @return the model with the data of the entity.
	 */
	public static NotificationPayload from(TopologyConnectionNotification entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new NotificationPayload();
			model.target = NodePayloadTest.from(entity.node);
			model.enabled = entity.enabled;
			model.converterJSCode = entity.notificationMessageConverterJSCode;
			return model;
		}
	}

}
