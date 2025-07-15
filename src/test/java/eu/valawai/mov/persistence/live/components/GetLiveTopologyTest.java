/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v2.live.topologies.LiveTopologyTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GetLiveTopology}.
 *
 * @see GetLiveTopology
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetLiveTopologyTest extends MovPersistenceTestCase {

	/**
	 * Should get the live topology.
	 */
	@Test
	public void shouldGetEmptyLiveTopology() {

		ComponentEntities.clear();
		final var found = this.assertItemNotNull(GetLiveTopology.fresh().execute());
		final var expected = LiveTopologyTest.current();
		assertEquals(expected, found);

	}

	/**
	 * Should get the live topology.
	 */
	@Test
	public void shouldGetLiveTopology() {

		final var connection = TopologyConnectionEntities.nextTopologyConnections(10).get(3);
		connection.deletedTimestamp = ValueGenerator.nextPastTime();
		this.assertItemNotNull(connection.update());

		final var component = ComponentEntities.nextComponents(10).get(3);
		component.finishedTime = ValueGenerator.nextPastTime();
		this.assertItemNotNull(component.update());

		final var found = this.assertItemNotNull(GetLiveTopology.fresh().execute());
		final var expected = LiveTopologyTest.current();
		LiveTopologyTest.sort(found);

//		for (var i = 0; i < expected.components.size(); i++) {
//			assertEquals(expected.components.get(i), found.components.get(i));
//		}

		assertEquals(expected, found);

	}

}
