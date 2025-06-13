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
import java.util.List;

import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link DisableAllTopologyConnections}.
 *
 * @see DisableAllTopologyConnections
 *
 * @author VALAWAI
 */
@QuarkusTest
public class DisableAllTopologyConnectionsTest extends MovPersistenceTestCase {

	/**
	 * Check that enable component.
	 */
	@Test
	public void shouldDisableAllTopologyConnections() {

		final var filter = Filters.and(Filters.eq("enabled", true),
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)));
		TopologyConnectionEntities.nextTopologyConnectionsUntil(filter, 10);
		final List<TopologyConnectionEntity> connections = this.assertItemNotNull(TopologyConnectionEntity
				.mongoCollection().find(filter, TopologyConnectionEntity.class).collect().asList());
		final var now = TimeManager.now();
		this.assertItemNull(DisableAllTopologyConnections.fresh().execute());

		for (final var connection : connections) {

			final TopologyConnectionEntity updated = (TopologyConnectionEntity) TopologyConnectionEntity
					.findById(connection.id).await().atMost(Duration.ofSeconds(30));
			assertNotNull(updated);
			assertFalse(updated.enabled);
			assertNotNull(updated.updateTimestamp);
			assertTrue(now <= updated.updateTimestamp);
		}

	}

}
