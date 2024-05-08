/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static eu.valawai.mov.ValueGenerator.nextJsonObject;
import static eu.valawai.mov.ValueGenerator.nextPastTime;

import eu.valawai.mov.events.PayloadTestCase;

/**
 * Test the {@link SentMessagePayload}.
 *
 * @see SentMessagePayload
 *
 * @author VALAWAI
 */
public class SentMessagePayloadTest extends PayloadTestCase<SentMessagePayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SentMessagePayload createEmptyModel() {

		return new SentMessagePayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(SentMessagePayload model) {

		final var builder = new MinComponentPayloadTest();
		model.source = builder.nextModel();
		model.target = builder.nextModel();
		model.messagePayload = nextJsonObject();
		model.timestamp = nextPastTime();
	}

}
