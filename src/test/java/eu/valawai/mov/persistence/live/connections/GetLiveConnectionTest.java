/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.connections;

import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v2.live.connections.LiveConnectionTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GetLiveConnection}.
 *
 * @see GetLiveConnection
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetLiveConnectionTest extends MovPersistenceTestCase {

	/**
	 * Should get {@code null} without connection.
	 */
	@Test
	public void shouldGetNullConnectionForNoCompnentId() {

		this.assertItemNullOrFailed(GetLiveConnection.fresh().execute());

	}

	/**
	 * Should get {@code null} with an undefined connection.
	 */
	@Test
	public void shouldGetNullConnectionForUndefinedConnection() {

		this.assertExecutionNull(GetLiveConnection.fresh().withConnection(nextObjectId()));

	}

	/**
	 * Should get a connection.
	 */
	@Test
	public void shouldGetConnection() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		final var result = this.assertExecutionNotNull(GetLiveConnection.fresh().withConnection(connection.id));
		final var expected = LiveConnectionTest.from(connection);
		assertEquals(expected, result);
	}

}
