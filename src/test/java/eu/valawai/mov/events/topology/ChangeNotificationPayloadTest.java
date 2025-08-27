/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextObjectId;

import eu.valawai.mov.events.PayloadTestCase;

/**
 * Test the {@link ChangeNotificationPayload}.
 *
 * @see ChangeNotificationPayload
 *
 * @author VALAWAI
 */
public class ChangeNotificationPayloadTest extends PayloadTestCase<ChangeNotificationPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChangeNotificationPayload createEmptyModel() {

		return new ChangeNotificationPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ChangeNotificationPayload payload) {

		payload.action = next(TopologyAction.values());
		payload.connectionId = nextObjectId();
		payload.target = new NodePayloadTest().nextModel();
	}

}
