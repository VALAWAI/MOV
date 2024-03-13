/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static eu.valawai.mov.ValueGenerator.nextUUID;
import static eu.valawai.mov.ValueGenerator.rnd;

import java.util.ArrayList;

import eu.valawai.mov.events.PayloadTestCase;

/**
 * Test the {@link ConnectionsPagePayload}.
 *
 * @see ConnectionsPagePayload
 *
 * @author VALAWAI
 */
public class ConnectionsPagePayloadTest extends PayloadTestCase<ConnectionsPagePayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionsPagePayload createEmptyModel() {

		return new ConnectionsPagePayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ConnectionsPagePayload payload) {

		payload.queryId = nextUUID().toString();
		payload.total = rnd().nextInt(0, 1001);
		final int max = rnd().nextInt(0, 10);
		if (max > 0) {

			payload.connections = new ArrayList<>();
			final var builder = new ConnectionPayloadTest();
			for (var i = 0; i < max; i++) {

				final var connection = builder.nextModel();
				payload.connections.add(connection);

			}
		}

	}

}
