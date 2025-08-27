/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.events.topology.TopologyAction;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link EnableTopologyConnection}.
 *
 * @see EnableTopologyConnection
 *
 * @author VALAWAI
 */
@QuarkusTest
public class EnableTopologyConnectionTest extends MovPersistenceTestCase {

	/**
	 * Check that cannot enable with undefined connection.
	 */
	@Test
	public void shouldNotEnableUndefinedConnection() {

		final var connectionId = TopologyConnectionEntities.undefined();
		final var result = this
				.assertItemNotNull(EnableTopologyConnection.fresh().withConnection(connectionId).execute());
		assertFalse(result);
	}

	/**
	 * Check that enable connection.
	 */
	@Test
	public void shouldEanableTopologyConnection() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.enabled = false;
		connection.update().await().atMost(Duration.ofSeconds(30));
		final var now = TimeManager.now();
		final var result = this.assertItemNotNull(EnableTopologyConnection.fresh().withConnection(connection.id)
				.withAction(TopologyAction.ENABLE).execute());
		assertTrue(result);

		final TopologyConnectionEntity updated = (TopologyConnectionEntity) TopologyConnectionEntity
				.findById(connection.id).await().atMost(Duration.ofSeconds(30));
		assertNotNull(updated);
		assertTrue(now <= updated.updateTimestamp);
		assertTrue(updated.enabled);

	}

	/**
	 * Check that disable connection.
	 */
	@Test
	public void shouldDisableTopologyConnection() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.enabled = true;
		connection.update().await().atMost(Duration.ofSeconds(30));
		final var now = TimeManager.now();
		final var result = this.assertItemNotNull(EnableTopologyConnection.fresh().withConnection(connection.id)
				.withAction(TopologyAction.DISABLE).execute());
		assertTrue(result);

		final TopologyConnectionEntity updated = (TopologyConnectionEntity) TopologyConnectionEntity
				.findById(connection.id).await().atMost(Duration.ofSeconds(30));
		assertNotNull(updated);
		assertTrue(now <= updated.updateTimestamp);
		assertFalse(updated.enabled);

	}

}
