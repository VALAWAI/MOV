/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link DeleteTopologyConnection}.
 *
 * @see DeleteTopologyConnection
 *
 * @author VALAWAI
 */
@QuarkusTest
public class DeleteTopologyConnectionTest extends MovPersistenceTestCase {

	/**
	 * Check that cannot enable with undefined connection.
	 */
	@Test
	public void shouldNotEnableUndefinedConnection() {

		final var connectionId = ValueGenerator.nextObjectId();
		final var result = this
				.assertItemNotNull(DeleteTopologyConnection.fresh().withConnection(connectionId).execute());
		assertFalse(result);
	}

	/**
	 * Check that enable connection.
	 */
	@Test
	public void shouldDeleteTopologyConnection() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.update().await().atMost(Duration.ofSeconds(30));
		final var now = TimeManager.now();
		final var result = this
				.assertItemNotNull(DeleteTopologyConnection.fresh().withConnection(connection.id).execute());
		assertTrue(result);

		final TopologyConnectionEntity updated = (TopologyConnectionEntity) TopologyConnectionEntity
				.findById(connection.id).await().atMost(Duration.ofSeconds(30));
		assertNotNull(updated);
		assertNotNull(updated.deletedTimestamp);
		assertTrue(now <= updated.deletedTimestamp);

	}

}
