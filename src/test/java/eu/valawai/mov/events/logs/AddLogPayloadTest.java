/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.logs;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextJsonPretty;
import static eu.valawai.mov.ValueGenerator.nextPattern;

import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.PayloadTestCase;

/**
 * Test the {@link AddLogPayload}.
 *
 * @see AddLogPayload
 *
 * @author VALAWAI
 */
public class AddLogPayloadTest extends PayloadTestCase<AddLogPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AddLogPayload createEmptyModel() {

		return new AddLogPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(AddLogPayload payload) {

		payload.level = next(LogLevel.values());
		payload.message = nextPattern("Log message {0}");
		payload.payload = nextJsonPretty();
	}

}
