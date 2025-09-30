/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;

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
import eu.valawai.mov.persistence.live.components.ComponentEntities;
import eu.valawai.mov.persistence.live.logs.LogEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionNotification;
import eu.valawai.mov.persistence.live.topology.TopologyNode;
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
	 *
	 * @param action that can not be done.
	 */
	@ParameterizedTest(name = "Should not change with {0} an undefined connection")
	@EnumSource(TopologyAction.class)
	public void shouldNotChangeTopologyWithUndefinedConnection(TopologyAction action) {

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = TopologyConnectionEntities.undefined();
		payload.action = action;

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
		assertEquals(2l,
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
	public void shouldEnableAndRemoveButNotRemoveASecondTime() {

		var connection = TopologyConnectionEntities.nextTopologyConnection();
		while (connection.enabled) {

			connection = TopologyConnectionEntities.nextTopologyConnection();
		}

		this.assertEnable(connection);

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = connection.id;
		payload.action = TopologyAction.REMOVE;
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.changeTopologyQueueName, payload));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.DEBUG,
								"Removed .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		final TopologyConnectionEntity current = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
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

	/**
	 * Check that a connection can be enabled.
	 *
	 * @param connection to enable.
	 *
	 * @return the enabled connection.
	 */
	private TopologyConnectionEntity assertEnable(TopologyConnectionEntity connection) {

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
		return current;

	}

	/**
	 * Check the message is converted using a java-script code.
	 */
	@Test
	public void shouldConvertMessageWithCode() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.enabled = false;
		connection.targetMessageConverterJSCode = """
				function convertEncodedMessage(source){

					MessageConverter_Builtins.debug("Debug {0}", source);
					var msg = JSON.parse(source);
					var connectionId = MessageConverter_Builtins.connectionId();
				    var target = {"message":msg,"connectionId":connectionId};
					return JSON.stringify(target);
				}
				export {convertEncodedMessage};
				""";
		this.assertItemNotNull(connection.update());
		this.assertEnable(connection);

		final var queue = this.waitOpenQueue(connection.target.channelName);
		final var source = ValueGenerator.nextJsonObject();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(connection.source.channelName, source));
		final var target = queue.waitReceiveMessage();
		assertThat(target.containsKey("message"), is(true));
		assertThat(target.getJsonObject("message"), is(source));
		assertThat(target.containsKey("connectionId"), is(true));
		assertThat(target.getString("connectionId"), is(connection.id.toHexString()));

	}

	/**
	 * Check the notification is converted using a java-script code.
	 */
	@Test
	public void shouldConvertNotificationWithCode() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.enabled = false;
		connection.notifications = new ArrayList<>();
		final var notification = new TopologyConnectionNotification();
		notification.enabled = true;
		notification.node = new TopologyNode();
		var targetNode = ComponentEntities.nextComponent();
		while (targetNode.channels == null || targetNode.channels.isEmpty()
				|| targetNode.channels.get(0).subscribe == null) {

			targetNode = ComponentEntities.nextComponent();
		}
		notification.node.componentId = targetNode.id;
		notification.node.channelName = targetNode.channels.get(0).name;
		notification.notificationMessageConverterJSCode = """
				function convertEncodedNotification(source){

					NotificationConverter_Builtins.debug("Debug {0}", source);
					var msg = JSON.parse(source);
					var connectionId = NotificationConverter_Builtins.connectionId();
				    var target = {
				    	"notification":msg,
				    	"connectionId":connectionId
				    };
					return JSON.stringify(target);
				}
				export {convertEncodedNotification};
				""";
		connection.notifications.add(notification);
		this.assertItemNotNull(connection.update());
		this.assertEnable(connection);

		final var queue = this.waitOpenQueue(notification.node.channelName);
		final var source = ValueGenerator.nextJsonObject();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(connection.source.channelName, source));
		final var target = queue.waitReceiveMessage();
		assertThat(target.containsKey("notification"), is(true));
		assertThat(target.getJsonObject("notification"), is(source));
		assertThat(target.containsKey("connectionId"), is(true));
		assertThat(target.getString("connectionId"), is(connection.id.toHexString()));
	}

	/**
	 * Check the notification is converted using the default with code.
	 */
	@Test
	public void shouldConvertNotificationWithCodeAsDefault() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.enabled = false;
		connection.notifications = new ArrayList<>();
		final var notification = new TopologyConnectionNotification();
		notification.enabled = true;
		notification.node = new TopologyNode();
		var targetNode = ComponentEntities.nextComponent();
		while (targetNode.channels == null || targetNode.channels.isEmpty()
				|| targetNode.channels.get(0).subscribe == null) {

			targetNode = ComponentEntities.nextComponent();
		}
		notification.node.componentId = targetNode.id;
		notification.node.channelName = targetNode.channels.get(0).name;
		notification.notificationMessageConverterJSCode = """
				function convertEncodedNotification(source){

					NotificationConverter_Builtins.debug("Debug {0}", source);
					var msg = JSON.parse(source);
					var connectionId = NotificationConverter_Builtins.connectionId();
				    var target = {
				    	"messagePayload":msg,
				    	"timestamp":NotificationConverter_Builtins.now(),
				    	"connectionId":connectionId,
				    	"source":{
				    		"id": NotificationConverter_Builtins.sourceId(),
				             "type": NotificationConverter_Builtins.sourceType(),
				             "name": NotificationConverter_Builtins.sourceName()
				    	},
				    	"target":{
				    		"id": NotificationConverter_Builtins.targetId(),
				             "type": NotificationConverter_Builtins.targetType(),
				             "name": NotificationConverter_Builtins.targetName()
				    	}
				    };
					return JSON.stringify(target);
				}
				export {convertEncodedNotification};
				""";
		connection.notifications.add(notification);
		this.assertItemNotNull(connection.update());
		this.assertEnable(connection);

		final var queue = this.waitOpenQueue(notification.node.channelName);
		final var source = ValueGenerator.nextJsonObject();
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(connection.source.channelName, source));
		final var target = queue.waitReceiveMessage();
		assertEquals(0l, LogEntity.count("level = ?1 and message like ?2 and timestamp >= ?3", LogLevel.ERROR,
				".*" + connection.toLogId() + ".*", now).await().atMost(Duration.ofSeconds(30)));

		assertThat(target.containsKey("messagePayload"), is(true));
		assertThat(target.getJsonObject("messagePayload"), is(source));
		assertThat(target.containsKey("connectionId"), is(true));
		assertThat(target.getString("connectionId"), is(connection.id.toHexString()));
		assertThat(target.containsKey("timestamp"), is(true));
		assertThat(target.getLong("timestamp"), is(greaterThanOrEqualTo(now)));
		assertThat(target.containsKey("source"), is(true));
		final var sourceOnConvertedMsg = target.getJsonObject("source");
		assertThat(sourceOnConvertedMsg.containsKey("id"), is(true));
		assertThat(sourceOnConvertedMsg.getString("id"), is(connection.source.componentId.toHexString()));
		assertThat(sourceOnConvertedMsg.containsKey("type"), is(true));
		assertThat(sourceOnConvertedMsg.getString("type"), is(connection.source.inferComponentType().name()));
		assertThat(sourceOnConvertedMsg.containsKey("name"), is(true));
		assertThat(sourceOnConvertedMsg.getString("name"), is(connection.source.inferComponentName()));
		assertThat(target.containsKey("source"), is(true));
		final var targetOnConvertedMsg = target.getJsonObject("target");
		assertThat(targetOnConvertedMsg.containsKey("id"), is(true));
		assertThat(targetOnConvertedMsg.getString("id"), is(connection.target.componentId.toHexString()));
		assertThat(targetOnConvertedMsg.containsKey("type"), is(true));
		assertThat(targetOnConvertedMsg.getString("type"), is(connection.target.inferComponentType().name()));
		assertThat(targetOnConvertedMsg.containsKey("name"), is(true));
		assertThat(targetOnConvertedMsg.getString("name"), is(connection.target.inferComponentName()));
	}

}
