/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v2.live.topologies.LiveTopology;
import eu.valawai.mov.api.v2.live.topologies.LiveTopologyComponentTest;
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
	 * Clear all the databases.
	 */
	@BeforeEach
	public void clear() {

		TopologyConnectionEntities.clear();
		ComponentEntities.clear();
	}

	/**
	 * Should get the live topology.
	 */
	@Test
	public void shouldGetEmptyLiveTopology() {

		final var found = this.assertItemNotNull(GetLiveTopology.fresh().execute());
		assertEquals(new LiveTopology(), found);

	}

	/**
	 * Should get the live topology with only component.
	 */
	@Test
	public void shouldGetLiveTopologyWithOnlyComponents() {

		final var expected = new LiveTopology();
		expected.components = new ArrayList<>();
		final var component = ComponentEntities.nextComponent();
		expected.components.add(LiveTopologyComponentTest.from(component));

		final var finishedComponent = ComponentEntities.nextComponent();
		finishedComponent.finishedTime = ValueGenerator.nextPastTime();
		this.assertItemNotNull(finishedComponent.update());

		var found = this.assertItemNotNull(GetLiveTopology.fresh().withOffset(1).withLimit(10).execute());
		assertEquals(new LiveTopology(), found);

		found = this.assertItemNotNull(GetLiveTopology.fresh().withOffset(0).withLimit(10).execute());
		LiveTopologyTest.sort(found);
		assertEquals(expected, found);

	}

	/**
	 * Should get the live topology with only component.
	 */
	@Test
	public void shouldGetLiveTopologyWithConnectionsWithoutNotifications() {

		TopologyConnectionEntities.nextTopologyConnection(0);

		final var deletedComponent = TopologyConnectionEntities.nextTopologyConnection(2);
		deletedComponent.deletedTimestamp = ValueGenerator.nextPastTime();
		this.assertItemNotNull(deletedComponent.update());

		final var found = this.assertItemNotNull(GetLiveTopology.fresh().withOffset(0).withLimit(10).execute());
		LiveTopologyTest.sort(found);
		final var expected = LiveTopologyTest.current(0, 10);
		assertEquals(expected, found);

	}

	/**
	 * Should get the live topology with only component.
	 */
	@Test
	public void shouldGetLiveTopologyWithConnectionsWithNotifications() {

		TopologyConnectionEntities.nextTopologyConnection(3);

		final var deletedComponent = TopologyConnectionEntities.nextTopologyConnection(0);
		deletedComponent.deletedTimestamp = ValueGenerator.nextPastTime();
		this.assertItemNotNull(deletedComponent.update());

		final var found = this.assertItemNotNull(GetLiveTopology.fresh().withOffset(0).withLimit(100).execute());
		LiveTopologyTest.sort(found);
		final var expected = LiveTopologyTest.current(0, 100);
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

		final var offset = 1;
		final var limit = 5;
		final var found = this.assertItemNotNull(GetLiveTopology.fresh().withOffset(offset).withLimit(limit).execute());
		LiveTopologyTest.sort(found);

		final var expected = LiveTopologyTest.current(offset, limit);
		assertEquals(expected, found);

	}

}
