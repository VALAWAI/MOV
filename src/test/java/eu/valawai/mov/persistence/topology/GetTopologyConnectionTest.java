/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v1.topology.TopologyConnectionTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GetTopologyConnection}.
 *
 * @see GetTopologyConnection
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetTopologyConnectionTest extends MovPersistenceTestCase {

	/**
	 * Should get {@code null} without connection.
	 */
	@Test
	public void shouldGetNullConnectionForNoCompnentId() {

		this.assertItemNullOrFailed(GetTopologyConnection.fresh().execute());

	}

	/**
	 * Should get {@code null} with an undefined connection.
	 */
	@Test
	public void shouldGetNullConnectionForUndefinedConnection() {

		this.assertExecutionNull(GetTopologyConnection.fresh().withConnection(nextObjectId()));

	}

	/**
	 * Should get a connection without subscriptions.
	 */
	@Test
	public void shouldGetConnectionWithoutSubscritions() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection(0);
		final var result = this.assertExecutionNotNull(GetTopologyConnection.fresh().withConnection(connection.id));
		final var expected = TopologyConnectionTest.from(connection);
		assertEquals(expected, result);
	}

	/**
	 * Should get a connection with subscriptions.
	 */
	@Test
	public void shouldGetConnectionWithSubscriptions() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection(rnd().nextInt(3, 23));
		final var result = this.assertExecutionNotNull(GetTopologyConnection.fresh().withConnection(connection.id));
		final var expected = TopologyConnectionTest.from(connection);
		assertEquals(expected, result);
	}

}
