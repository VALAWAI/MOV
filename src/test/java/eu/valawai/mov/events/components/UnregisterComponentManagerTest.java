/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.components.ComponentEntities;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.logs.LogEntity;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;

/**
 * Test the {@link UnregisterComponentManager}.
 *
 * @see UnregisterComponentManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class UnregisterComponentManagerTest extends MovEventTestCase {

	/**
	 * The name of the queue to send the unregister component events.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.unregister_component.queue.name", defaultValue = "valawai/component/unregister")
	String unregisterComponentQueueName;

	/**
	 * Check that cannot unregister with an invalid payload.
	 */
	@Test
	public void shouldNotUnregisterComponentWithInvalidPayload() {

		final var payload = new UnregisterComponentPayload();
		final var countComponents = ComponentEntity.count().await().atMost(Duration.ofSeconds(30));

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.unregisterComponentQueueName, payload));

		assertEquals(1l,
				this.assertItemNotNull(LogEntity.count("level = ?1 and timestamp >= ?2", LogLevel.ERROR, now)));
		assertEquals(countComponents, ComponentEntity.count().await().atMost(Duration.ofSeconds(30)));
	}

	/**
	 * Check that cannot unregister a non registered component.
	 */
	@Test
	public void shouldNotUnregisterComponentThatIsNotRegistered() {

		final var payload = new UnregisterComponentPayloadTest().nextModel();
		final var countComponents = ComponentEntity.count().await().atMost(Duration.ofSeconds(30));

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.unregisterComponentQueueName, payload));

		final LogEntity log = this.assertItemNotNull(LogEntity.findAll(Sort.descending("_id")).firstResult());
		assertEquals(LogLevel.ERROR, log.level);
		final var logPayload = Json.decodeValue(log.payload, UnregisterComponentPayload.class);
		assertEquals(payload, logPayload);
		assertEquals(countComponents, ComponentEntity.count().await().atMost(Duration.ofSeconds(30)));
	}

	/**
	 * Check that a component is created and is created a connection as the
	 * unregistered component as source.
	 */
	@Test
	public void shouldUnregisterComponent() {

		final var component = ComponentEntities.nextComponent();
		final List<TopologyConnectionEntity> connections = new ArrayList<>();
		final var max = 25;
		for (var i = 0; i < max; i++) {

			final var connection = TopologyConnectionEntities.nextTopologyConnection();
			connection.enabled = true;
			if (flipCoin()) {

				connection.source.componentId = component.id;

			} else {

				connection.target.componentId = component.id;
			}
			this.assertItemNotNull(connection.update());
			connections.add(connection);
			this.waitOpenQueue(connection.source.channelName);

		}

		final var payload = new UnregisterComponentPayload();
		payload.componentId = component.id;

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLogs(max + 1, () -> this.assertPublish(this.unregisterComponentQueueName, payload));
		assertEquals(max + 1l,
				this.assertItemNotNull(LogEntity.count("level = ?1 and timestamp >= ?2", LogLevel.INFO, now)));

		final ComponentEntity updated = this.assertItemNotNull(ComponentEntity.findById(component.id));
		assertNotNull(updated.finishedTime);
		assertTrue(now <= updated.finishedTime);

		for (final var connection : connections) {

			final TopologyConnectionEntity updatedConnection = this
					.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
			assertNotNull(updatedConnection.deletedTimestamp);
			assertTrue(now <= updatedConnection.deletedTimestamp);
			assertFalse(this.listener.isOpen(connection.source.channelName));

		}

	}

}
