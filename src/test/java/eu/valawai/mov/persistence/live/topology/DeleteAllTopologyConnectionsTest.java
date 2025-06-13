/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link DeleteAllTopologyConnections}.
 *
 * @see DeleteAllTopologyConnections
 *
 * @author VALAWAI
 */
@QuarkusTest
public class DeleteAllTopologyConnectionsTest extends MovPersistenceTestCase {

	/**
	 * Check that enable component.
	 */
	@Test
	public void shouldDeleteAllTopologyConnections() {

		final var components = TopologyConnectionEntities.nextTopologyConnections(10);
		final var now = TimeManager.now();
		this.assertItemNull(DeleteAllTopologyConnections.fresh().execute());

		for (final var component : components) {

			final TopologyConnectionEntity updated = (TopologyConnectionEntity) TopologyConnectionEntity
					.findById(component.id).await().atMost(Duration.ofSeconds(30));
			assertNotNull(updated);
			assertNotNull(updated.deletedTimestamp);
			assertTrue(now <= updated.deletedTimestamp);
		}

	}

}
