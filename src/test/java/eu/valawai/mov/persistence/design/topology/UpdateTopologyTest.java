/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.topology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v2.design.topologies.TopologyTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link UpdateTopology}.
 *
 * @see UpdateTopology
 *
 * @author VALAWAI
 */
@QuarkusTest
public class UpdateTopologyTest extends MovPersistenceTestCase {

	/**
	 * Should not get from undefined topology.
	 */
	@Test
	public void shouldNotUpdateUndefinedTopology() {

		final var undefined = TopologyGraphEntities.undefined();
		final var topology = TopologyTest.from(TopologyGraphEntities.minTopologies(1).get(0));

		final var updated = this
				.assertItemNotNull(UpdateTopology.fresh().withId(undefined).withTopology(topology).execute());
		assertFalse(updated, "Updated an undefined topology");

	}

	/**
	 * Should update topology.
	 */
	@Test
	public void shouldUpdateTopology() {

		final var entities = TopologyGraphEntities.minTopologies(2);
		final var targetId = entities.get(1).id;
		final var topology = TopologyTest.from(entities.get(0));
		final var updated = this
				.assertItemNotNull(UpdateTopology.fresh().withId(targetId).withTopology(topology).execute());
		assertTrue(updated, "Not updated a topology");

		final TopologyGraphEntity unmodified = this.assertItemNotNull(TopologyGraphEntity.findById(topology.id));
		final var unmodifiedTopology = TopologyTest.from(unmodified);
		assertEquals(updated, unmodifiedTopology);

		final TopologyGraphEntity current = this.assertItemNotNull(TopologyGraphEntity.findById(targetId));
		final var currentTopology = TopologyTest.from(current);
		assertEquals(updated, currentTopology);

	}

}
