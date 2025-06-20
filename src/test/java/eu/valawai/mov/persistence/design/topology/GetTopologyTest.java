/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.topology;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v2.design.topologies.TopologyTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GetTopology}.
 *
 * @see GetTopology
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetTopologyTest extends MovPersistenceTestCase {

	/**
	 * Should not get from undefined topology.
	 */
	@Test
	public void shouldNotGetUndefinedTopology() {

		final var undefined = TopologyGraphEntities.undefined();
		this.assertItemIsNull(GetTopology.fresh().withId(undefined).execute());

	}

	/**
	 * Should get topology.
	 */
	@Test
	public void shouldGetTopology() {

		final var entity = TopologyGraphEntities.minTopologies(1).get(0);
		final var found = this.assertItemNotNull(GetTopology.fresh().withId(entity.id).execute());
		final var expected = TopologyTest.from(entity);
		assertEquals(found, expected);
	}

}
