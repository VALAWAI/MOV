/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.nextEchoConvertJSCode;
import static eu.valawai.mov.ValueGenerator.nextObjectId;

import eu.valawai.mov.events.PayloadTestCase;

/**
 * Test the {@link CreateNotificationPayload}.
 *
 * @see CreateNotificationPayload
 *
 * @author VALAWAI
 */
public class CreateNotificationPayloadTest extends PayloadTestCase<CreateNotificationPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CreateNotificationPayload createEmptyModel() {

		return new CreateNotificationPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(CreateNotificationPayload payload) {

		final var builder = new NodePayloadTest();
		payload.target = builder.nextModel();
		payload.enabled = flipCoin();
		payload.converterJSCode = nextEchoConvertJSCode();
		payload.connectionId = nextObjectId();
	}

}
