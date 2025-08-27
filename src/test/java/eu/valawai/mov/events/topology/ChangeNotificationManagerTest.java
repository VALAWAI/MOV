/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.events.RabbitMQService;
import eu.valawai.mov.events.TestQueue;
import eu.valawai.mov.persistence.live.logs.LogEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import eu.valawai.mov.persistence.live.topology.TopologyNode;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;
import jakarta.inject.Inject;

/**
 * Test the {@link ChangeNotificationManager}.
 *
 * @see ChangeNotificationManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class ChangeNotificationManagerTest extends MovEventTestCase {

	/**
	 * The queue name to send the change topology events.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.change_topology_notification.queue.name", defaultValue = "valawai/topology/notification/change")
	String changeTopologyNotificationQueueName;

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

		final var payload = new ChangeNotificationPayload();
		payload.connectionId = ValueGenerator.nextObjectId();

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyNotificationQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
	}

	/**
	 * Check that cannot do an action over an undefined connection.
	 *
	 * @param action that can not be done.
	 */
	@ParameterizedTest(name = "Should not do action {0} an undefined connection.")
	@EnumSource(TopologyAction.class)
	public void shouldNotDoActionOverUndefinedConnection(TopologyAction action) {

		final var payload = new ChangeNotificationPayloadTest().nextModel();
		payload.connectionId = TopologyConnectionEntities.undefined();
		payload.action = action;

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyNotificationQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
	}

	/**
	 * Check that cannot do an action over a connection without notifications.
	 *
	 * @param action that can not be done.
	 */
	@ParameterizedTest(name = "Should not do action {0} over a connection without notifications.")
	@EnumSource(TopologyAction.class)
	public void shouldNotDoActionOverConnectionWithoutNotifications(TopologyAction action) {

		final var payload = new ChangeNotificationPayloadTest().nextModel();
		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.notifications = null;
		this.assertItemNotNull(connection.update());
		payload.connectionId = connection.id;
		payload.action = action;

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyNotificationQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
	}

	/**
	 * Check that cannot do an action over an undefined notification.
	 *
	 * @param action that can not be done.
	 */
	@ParameterizedTest(name = "Should not do action {0} over an undefined notification.")
	@EnumSource(TopologyAction.class)
	public void shouldNotDoActionOverUndefinedtNotifications(TopologyAction action) {

		final var payload = new ChangeNotificationPayloadTest().nextModel();
		final var connection = TopologyConnectionEntities.nextTopologyConnection(3);
		NOTIFICATIONS: while (connection.notifications != null) {

			for (final var notification : connection.notifications) {

				if (notification.node.componentId.equals(payload.target.componentId)
						&& notification.node.channelName.equals(payload.target.channelName)) {

					payload.target = new NodePayloadTest().nextModel();
					continue NOTIFICATIONS;
				}
			}
			break;

		}
		payload.connectionId = connection.id;
		payload.action = action;

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyNotificationQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));

	}

	/**
	 * Check that cannot do an action over a deleted connection.
	 *
	 * @param action that can not be done.
	 */
	@ParameterizedTest(name = "Should not do action {0} over a deleted connection.")
	@EnumSource(TopologyAction.class)
	public void shouldNotDoActionOverDeletedConnection(TopologyAction action) {

		final var connection = TopologyConnectionEntities.nextTopologyConnection(3);
		final TopologyNode target = ValueGenerator.next(connection.notifications).node;
		connection.deletedTimestamp = ValueGenerator.nextPastTime();
		this.assertItemNotNull(connection.update());

		final var payload = new ChangeNotificationPayload();
		payload.connectionId = connection.id;
		payload.action = action;
		payload.target = NodePayloadTest.from(target);

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyNotificationQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));

	}

	/**
	 * Check that enable notification.
	 */
	@Test
	public void shouldEanableNotification() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection(3);
		final TopologyNode target = connection.notifications.get(1).node;
		connection.notifications.get(1).enabled = false;
		this.assertItemNotNull(connection.update());

		final var payload = new ChangeNotificationPayload();
		payload.connectionId = connection.id;
		payload.action = TopologyAction.ENABLE;
		payload.target = NodePayloadTest.from(target);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyNotificationQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.DEBUG, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= updated.updateTimestamp);
		assertTrue(updated.notifications.get(1).enabled);

	}

	/**
	 * Check that disable notification.
	 */
	@Test
	public void shouldDisableNotification() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection(3);
		final TopologyNode target = connection.notifications.get(1).node;
		connection.notifications.get(1).enabled = true;
		this.assertItemNotNull(connection.update());

		final var payload = new ChangeNotificationPayload();
		payload.connectionId = connection.id;
		payload.action = TopologyAction.DISABLE;
		payload.target = NodePayloadTest.from(target);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyNotificationQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.DEBUG, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= updated.updateTimestamp);
		assertFalse(updated.notifications.get(1).enabled);

	}

	/**
	 * Check that remove notification.
	 */
	@Test
	public void shouldRemoveNotification() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection(3);
		final TopologyNode target = connection.notifications.get(1).node;
		connection.notifications.get(1).enabled = true;
		this.assertItemNotNull(connection.update());

		final var payload = new ChangeNotificationPayload();
		payload.connectionId = connection.id;
		payload.action = TopologyAction.REMOVE;
		payload.target = NodePayloadTest.from(target);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyNotificationQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.DEBUG, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= updated.updateTimestamp);
		assertThat(updated.notifications, hasSize(2));
		assertThat(updated.notifications, not(hasItem(connection.notifications.get(1))));

	}

}
