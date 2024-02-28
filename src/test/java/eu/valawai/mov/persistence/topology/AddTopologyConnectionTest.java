/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;

/**
 * Test the {@link AddTopologyConnection}.
 *
 * @see AddTopologyConnection
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
public class AddTopologyConnectionTest extends MovPersistenceTestCase {

	/**
	 * Check that not add a connection if it is already defined.
	 */
	@Test
	public void shouldNotAddDuplicatedTopologyConnection() {

		final var builder = new TopologyNodeTest();
		final var source = builder.nextModel();
		final var target = builder.nextModel();
		final var suplicatedEliminatedEntity = new TopologyConnectionEntity();
		suplicatedEliminatedEntity.source = source;
		suplicatedEliminatedEntity.target = target;
		suplicatedEliminatedEntity.createTimestamp = ValueGenerator.nextPastTime();
		suplicatedEliminatedEntity.updateTimestamp = ValueGenerator.nextPastTime();
		suplicatedEliminatedEntity.persist().await().atMost(Duration.ofSeconds(30));
		assertNotNull(suplicatedEliminatedEntity.id);

		final var connectionId = AddTopologyConnection.fresh().withSourceComponent(source.componentId)
				.withSourceChannel(source.channelName).withTargetComponent(target.componentId)
				.withTargetChannel(target.channelName).execute().await().atMost(Duration.ofSeconds(30));
		assertNull(connectionId);

	}

	/**
	 * Check that add a connection.
	 */
	@Test
	public void shouldAddTopologyConnection() {

		final var builder = new TopologyNodeTest();
		final var source = builder.nextModel();
		final var target = builder.nextModel();
		final var now = TimeManager.now();
		final var connectionId = AddTopologyConnection.fresh().withSourceComponent(source.componentId)
				.withSourceChannel(source.channelName).withTargetComponent(target.componentId)
				.withTargetChannel(target.channelName).execute().await().atMost(Duration.ofSeconds(30));
		assertNotNull(connectionId);

		final Uni<TopologyConnectionEntity> find = TopologyConnectionEntity.findById(connectionId);
		final var entity = find.await().atMost(Duration.ofSeconds(30));
		assertNotNull(entity);
		assertTrue(now <= entity.createTimestamp);
		assertTrue(now <= entity.updateTimestamp);
		assertNull(entity.deletedTimestamp);
		assertTrue(entity.enabled);
		assertEquals(source, entity.source);
		assertEquals(target, entity.target);

	}

	/**
	 * Check add a connection if the already defined is deleted.
	 */
	@Test
	public void shouldAddTopologyConnectionIfDuplicatedIsRemoved() {

		final var builder = new TopologyNodeTest();
		final var source = builder.nextModel();
		final var target = builder.nextModel();
		final var suplicatedEliminatedEntity = new TopologyConnectionEntity();
		suplicatedEliminatedEntity.source = source;
		suplicatedEliminatedEntity.target = target;
		suplicatedEliminatedEntity.createTimestamp = ValueGenerator.nextPastTime();
		suplicatedEliminatedEntity.updateTimestamp = ValueGenerator.nextPastTime();
		suplicatedEliminatedEntity.deletedTimestamp = ValueGenerator.nextPastTime();
		suplicatedEliminatedEntity.persist().await().atMost(Duration.ofSeconds(30));
		assertNotNull(suplicatedEliminatedEntity.id);

		final var now = TimeManager.now();
		final var connectionId = AddTopologyConnection.fresh().withSourceComponent(source.componentId)
				.withSourceChannel(source.channelName).withTargetComponent(target.componentId)
				.withTargetChannel(target.channelName).execute().await().atMost(Duration.ofSeconds(30));
		assertNotNull(connectionId);

		final Uni<TopologyConnectionEntity> find = TopologyConnectionEntity.findById(connectionId);
		final var entity = find.await().atMost(Duration.ofSeconds(30));
		assertNotNull(entity);
		assertTrue(now <= entity.createTimestamp);
		assertTrue(now <= entity.updateTimestamp);
		assertNull(entity.deletedTimestamp);
		assertTrue(entity.enabled);
		assertEquals(source, entity.source);
		assertEquals(target, entity.target);

	}

}
