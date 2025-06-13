/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.nextPastTime;
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
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.components.BasicPayloadSchemaTest;
import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ObjectPayloadSchema;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.live.components.ComponentEntities;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.logs.LogEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import eu.valawai.mov.persistence.live.topology.TopologyNode;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
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
	 * Check that when unregister a component all the connections that it is
	 * involved are disabled.
	 */
	@Test
	public void shouldUnregisterComponent() {

		final var component = ComponentEntities.nextComponent();
		component.channels = new ArrayList<>();
		final List<TopologyConnectionEntity> connections = new ArrayList<>();
		final var max = 25;
		for (var i = 0; i < max; i++) {

			final var connection = new TopologyConnectionEntity();
			connection.enabled = true;
			connection.source = new TopologyNode();
			connection.target = new TopologyNode();
			connection.createTimestamp = nextPastTime();
			connection.updateTimestamp = nextPastTime();

			final var otherComponent = ComponentEntities.nextComponent();
			otherComponent.channels = new ArrayList<>();

			final var componentChannel = new ChannelSchema();
			componentChannel.name = "valawai/" + component.type.name().toLowerCase() + "/"
					+ component.name.toLowerCase() + "/data/test_" + i;
			component.channels.add(componentChannel);

			final var otherComponentChannel = new ChannelSchema();
			otherComponentChannel.name = "valawai/" + otherComponent.type.name().toLowerCase() + "/"
					+ otherComponent.name.toLowerCase() + "/data/test_" + i;
			otherComponent.channels.add(otherComponentChannel);

			final var channelContent = new ObjectPayloadSchema();
			channelContent.properties.put("property_" + ValueGenerator.nextObjectId().toHexString() + "_" + i,
					new BasicPayloadSchemaTest().nextModel());
			if (flipCoin()) {

				connection.source.componentId = component.id;
				connection.source.channelName = componentChannel.name;
				connection.target.componentId = otherComponent.id;
				connection.target.channelName = otherComponentChannel.name;
				componentChannel.publish = channelContent;
				otherComponentChannel.subscribe = channelContent;

			} else {

				connection.target.componentId = component.id;
				connection.target.channelName = componentChannel.name;
				connection.source.componentId = otherComponent.id;
				connection.source.channelName = otherComponentChannel.name;
				componentChannel.subscribe = channelContent;
				otherComponentChannel.publish = channelContent;

			}

			this.assertItemNotNull(otherComponent.update());
			this.assertItemNotNull(connection.persist());
			this.waitOpenQueue(connection.source.channelName);
			connections.add(connection);
		}

		this.assertItemNotNull(component.update());

		final var payload = new UnregisterComponentPayload();
		payload.componentId = component.id;

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.unregisterComponentQueueName, payload));

		this.waitUntilNotNull(() -> {

			final Uni<ComponentEntity> find = ComponentEntity.findById(component.id);
			return find;

		}, updated -> updated.finishedTime != null);

		final ComponentEntity updated = this.assertItemNotNull(ComponentEntity.findById(component.id));
		assertNotNull(updated.finishedTime);
		assertTrue(now <= updated.finishedTime);

		for (final var connection : connections) {

			this.waitUntilNotNull(() -> {

				final Uni<TopologyConnectionEntity> find = TopologyConnectionEntity.findById(connection.id);
				return find;

			}, updatedConnection -> updatedConnection.deletedTimestamp != null);

			final TopologyConnectionEntity updatedConnection = this
					.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
			assertNotNull(updatedConnection.deletedTimestamp);
			assertTrue(now <= updatedConnection.deletedTimestamp);
			assertFalse(this.listener.isOpen(connection.source.channelName));

		}

	}

}
