/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.events.RabbitMQService;
import eu.valawai.mov.events.TestQueue;
import eu.valawai.mov.persistence.logs.LogEntity;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;

/**
 * Test the {@link ChangeTopologyManager}.
 *
 * @see ChangeTopologyManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class ChangeTopologyManagerTest extends MovEventTestCase {

	/**
	 * The queue name to send the change topology events.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.change_topology.queue.name", defaultValue = "valawai/topology/change")
	String changeTopologyQueueName;

	/**
	 * The Rabbit MQ service.
	 */
	@Inject
	RabbitMQService service;

	/**
	 * A component that receive messages form a queue used for testing.
	 */
	@Inject
	TestQueue testQueue;

	/**
	 * Check that cannot change with an invalid payload.
	 */
	@Test
	public void shouldNotChangeTopologyWithInvalidPayload() {

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = ValueGenerator.nextObjectId();

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
	}

	/**
	 * Check that cannot change with an undefined connection.
	 */
	@Test
	public void shouldNotChangeTopologyWithUndefinedConnection() {

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = ValueGenerator.nextObjectId();
		payload.action = ValueGenerator.next(TopologyAction.values());

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
	}

	/**
	 * Check that cannot change with an undefined connection.
	 */
	@Test
	public void shouldNotDisableADisabledConnection() {

		var connection = TopologyConnectionEntities.nextTopologyConnection();
		while (connection.enabled) {

			connection = TopologyConnectionEntities.nextTopologyConnection();
		}

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = connection.id;
		payload.action = TopologyAction.DISABLE;

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));

		assertEquals(1l, LogEntity
				.count("level = ?1 and message like ?2", LogLevel.ERROR, ".*" + connection.id.toHexString() + ".*")
				.await().atMost(Duration.ofSeconds(30)));

		final TopologyConnectionEntity current = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertEquals(connection.updateTimestamp, current.updateTimestamp);
		assertEquals(connection.enabled, current.enabled);
	}

	/**
	 * Check that can enable and disable a connection.
	 */
	@Test
	public void shouldEnableAndDisable() {

		var connection = TopologyConnectionEntities.nextTopologyConnection();
		while (connection.enabled) {

			connection = TopologyConnectionEntities.nextTopologyConnection();
		}

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = connection.id;
		payload.action = TopologyAction.ENABLE;

		var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.INFO,
								"Enabled .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		TopologyConnectionEntity current = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= current.updateTimestamp);
		assertTrue(current.enabled);
		assertTrue(this.listener.isOpen(connection.source.channelName));

		payload.action = TopologyAction.DISABLE;
		now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.INFO,
								"Disabled .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		current = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= current.updateTimestamp);
		assertFalse(current.enabled);
		assertFalse(this.listener.isOpen(connection.source.channelName));
	}

	/**
	 * Check that can enable and disable a connection but not disable a second time.
	 */
	@Test
	public void shouldEnableAndDisableButNotdisableAsecondTime() {

		var connection = TopologyConnectionEntities.nextTopologyConnection();
		while (connection.enabled) {

			connection = TopologyConnectionEntities.nextTopologyConnection();
		}

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = connection.id;
		payload.action = TopologyAction.ENABLE;

		var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.INFO,
								"Enabled .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		TopologyConnectionEntity current = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= current.updateTimestamp);
		assertTrue(current.enabled);
		assertTrue(this.listener.isOpen(connection.source.channelName));

		payload.action = TopologyAction.DISABLE;
		now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.INFO,
								"Disabled .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		current = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= current.updateTimestamp);
		assertFalse(current.enabled);
		assertFalse(this.listener.isOpen(connection.source.channelName));

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l, LogEntity
				.count("level = ?1 and message like ?2", LogLevel.ERROR, ".*" + connection.id.toHexString() + ".*")
				.await().atMost(Duration.ofSeconds(30)));

		final TopologyConnectionEntity last = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertEquals(current.updateTimestamp, last.updateTimestamp);
		assertEquals(current.enabled, last.enabled);
		assertFalse(this.listener.isOpen(connection.source.channelName));

	}

	/**
	 * Check that can not enable two times the same connection.
	 */
	@Test
	public void shouldNotEnableTwoTimesTheSameConnection() {

		var connection = TopologyConnectionEntities.nextTopologyConnection();
		while (connection.enabled) {

			connection = TopologyConnectionEntities.nextTopologyConnection();
		}

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = connection.id;
		payload.action = TopologyAction.ENABLE;

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.INFO,
								"Enabled .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		final TopologyConnectionEntity current = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= current.updateTimestamp);
		assertTrue(current.enabled);
		assertTrue(this.listener.isOpen(connection.source.channelName));

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l, LogEntity
				.count("level = ?1 and message like ?2", LogLevel.ERROR, ".*" + connection.id.toHexString() + ".*")
				.await().atMost(Duration.ofSeconds(30)));

		final TopologyConnectionEntity last = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertEquals(current.updateTimestamp, last.updateTimestamp);
		assertEquals(current.enabled, last.enabled);
		assertTrue(this.listener.isOpen(connection.source.channelName));

	}

	/**
	 * Check that can enable a connection and the message is received by the target.
	 */
	@Test
	public void shouldEnableAndReceivePassThroughtMessage() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.enabled = false;
		final var targetQueueName = this.testQueue.getInputQueueName();
		connection.target.channelName = targetQueueName;
		this.assertItemNotNull(connection.update());

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = connection.id;
		payload.action = TopologyAction.ENABLE;

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.INFO,
								"Enabled .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		final TopologyConnectionEntity current = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= current.updateTimestamp);
		assertTrue(current.enabled);
		assertTrue(this.listener.isOpen(connection.source.channelName));

		final var msgPayload = new JsonObject();
		msgPayload.put("pattern", ValueGenerator.nextPattern("pattern {0}"));
		final var sourceQueue = connection.source.channelName;
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(sourceQueue, msgPayload));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2 and payload = ?3", LogLevel.DEBUG,
								".*" + sourceQueue + ".+" + targetQueueName + ".*", Json.encodePrettily(msgPayload))
						.await().atMost(Duration.ofSeconds(30)));

		final var received = TestQueue.waitForPayload();
		assertEquals(msgPayload, received);

	}

	/**
	 * Check that cannot remove a finished connection.
	 */
	@Test
	public void shouldNotRemoveADeletedConnection() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.deletedTimestamp = TimeManager.now();
		this.assertItemNotNull(connection.update());

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = connection.id;
		payload.action = TopologyAction.REMOVE;

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));

		assertEquals(1l, LogEntity
				.count("level = ?1 and message like ?2", LogLevel.ERROR, ".*" + connection.id.toHexString() + ".*")
				.await().atMost(Duration.ofSeconds(30)));

		final TopologyConnectionEntity current = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertEquals(connection.updateTimestamp, current.updateTimestamp);
		assertEquals(connection.enabled, current.enabled);
	}

	/**
	 * Check that can enable and remove a connection.
	 */
	@Test
	public void shouldEnableAndRemove() {

		var connection = TopologyConnectionEntities.nextTopologyConnection();
		while (connection.enabled) {

			connection = TopologyConnectionEntities.nextTopologyConnection();
		}

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = connection.id;
		payload.action = TopologyAction.ENABLE;

		var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.INFO,
								"Enabled .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		TopologyConnectionEntity current = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= current.updateTimestamp);
		assertTrue(current.enabled);
		assertTrue(this.listener.isOpen(connection.source.channelName));

		payload.action = TopologyAction.REMOVE;
		now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.DEBUG,
								"Removed .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		current = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertNotNull(current.deletedTimestamp);
		assertTrue(now <= current.deletedTimestamp);
		assertFalse(this.listener.isOpen(connection.source.channelName));
	}

	/**
	 * Check that can enable and remove a connection but not remove a second time.
	 */
	@Test
	public void shouldEnableAndRemoveButNotremoveAsecondTime() {

		var connection = TopologyConnectionEntities.nextTopologyConnection();
		while (connection.enabled) {

			connection = TopologyConnectionEntities.nextTopologyConnection();
		}

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = connection.id;
		payload.action = TopologyAction.ENABLE;

		var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.INFO,
								"Enabled .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		TopologyConnectionEntity current = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= current.updateTimestamp);
		assertTrue(current.enabled);
		assertTrue(this.listener.isOpen(connection.source.channelName));

		payload.action = TopologyAction.REMOVE;
		now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.DEBUG,
								"Removed .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		current = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertNotNull(current.deletedTimestamp);
		assertTrue(now <= current.deletedTimestamp);
		assertFalse(this.listener.isOpen(connection.source.channelName));

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l, LogEntity
				.count("level = ?1 and message like ?2", LogLevel.ERROR, ".*" + connection.id.toHexString() + ".*")
				.await().atMost(Duration.ofSeconds(30)));

		final TopologyConnectionEntity last = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertEquals(current.updateTimestamp, last.updateTimestamp);
		assertEquals(current.enabled, last.enabled);
		assertFalse(this.listener.isOpen(connection.source.channelName));

	}
}
