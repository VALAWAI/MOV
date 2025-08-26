/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.events.topology.SentMessagePayload.createSentMessagePayloadSchemaFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v1.components.PayloadSchema;
import eu.valawai.mov.api.v1.components.PayloadSchemaTestCase;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.live.components.ComponentEntities;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.logs.LogEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * Test the {@link CreateConnectionManager}.
 *
 * @see CreateConnectionManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class CreateConnectionManagerTest extends MovEventTestCase {

	/**
	 * The queue name to send the create connection events.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.create_connection.queue.name", defaultValue = "valawai/topology/change")
	String createConnectionQueueName;

	/**
	 * Check that cannot create with an invalid paload.
	 */
	@Test
	public void shouldNotCreateConnectionWithInvalidPayload() {

		final var payload = new CreateConnectionPayload();
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createConnectionQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with an undefined source.
	 */
	@Test
	public void shouldNotCreateConnectionWithUndefinedSourceComponentId() {

		final var payload = new CreateConnectionPayloadTest().nextModel();
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createConnectionQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with an undefined source.
	 */
	@Test
	public void shouldNotCreateConnectionWithUndefinedSourceChannel() {

		final var payload = new CreateConnectionPayloadTest().nextModel();
		final var component = ComponentEntities.nextComponent();
		payload.source.componentId = component.id;
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createConnectionQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with a undefined publish source channel.
	 */
	@Test
	public void shouldNotCreateConnectionWithNotPubblishSourceChannel() {

		final var payload = new CreateConnectionPayloadTest().nextModel();
		payload.source.componentId = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish == null && channel.subscribe != null) {

						payload.source.componentId = component.id;
						payload.source.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.source.componentId == null);
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createConnectionQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with an undefined target component.
	 */
	@Test
	public void shouldNotCreateConnectionWithUndefinedTargetComponentId() {

		final var payload = new CreateConnectionPayloadTest().nextModel();
		payload.source.componentId = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null) {

						payload.source.componentId = component.id;
						payload.source.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.source.componentId == null);
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createConnectionQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with an undefined target channel.
	 */
	@Test
	public void shouldNotCreateConnectionWithUndefinedTargetChannel() {

		final var payload = new CreateConnectionPayloadTest().nextModel();
		payload.source.componentId = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null) {

						payload.source.componentId = component.id;
						payload.source.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.source.componentId == null);
		payload.target.componentId = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						payload.target.componentId = component.id;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createConnectionQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with an undefined target channel.
	 */
	@Test
	public void shouldNotCreateConnectionWithNotSubscribedTargetChannel() {

		final var payload = new CreateConnectionPayloadTest().nextModel();
		payload.source.componentId = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null) {

						payload.source.componentId = component.id;
						payload.source.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.source.componentId == null);
		payload.target.componentId = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null && channel.subscribe == null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createConnectionQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create a connection where the publish and subscribe do not
	 * match.
	 */
	@Test
	public void shouldNotCreateConnectionWithPublishNotMatchsubscribe() {

		final var payload = new CreateConnectionPayloadTest().nextModel();
		payload.source.componentId = null;
		payload.target_message_converter_js_code = null;
		PayloadSchema publish = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null) {

						payload.source.componentId = component.id;
						payload.source.channelName = channel.name;
						publish = channel.publish;
						break;

					}
				}
			}

		} while (payload.source.componentId == null);
		payload.target.componentId = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null && !publish.match(channel.subscribe, new HashMap<>())) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createConnectionQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create an existing connection.
	 */
	@Test
	public void shouldNotCreateConnectionThatAlreadyExists() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		final var payload = new CreateConnectionPayload();
		payload.source = NodePayloadTest.from(connection.source);
		payload.target = NodePayloadTest.from(connection.target);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createConnectionQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));

	}

	/**
	 * Check that create a connection and enable it.
	 */
	@Test
	public void shouldCreateConnectionAndEnableConnection() {

		final var payload = this.createValidCreateConnectionPayload();
		payload.enabled = true;
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLogs(2, () -> this.assertPublish(this.createConnectionQueueName, payload));

		final TopologyConnectionEntity last = this
				.assertItemNotNull(TopologyConnectionEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= last.createTimestamp);
		assertTrue(last.createTimestamp <= last.updateTimestamp);
		assertNull(last.deletedTimestamp);
		assertEquals(payload.source, NodePayloadTest.from(last.source));
		assertEquals(payload.target, NodePayloadTest.from(last.target));
		assertTrue(last.enabled);

		assertTrue(this.listener.isOpen(payload.source.channelName));

		assertEquals(2l, this.assertItemNotNull(LogEntity.count("level = ?1 and message like ?2 and timestamp >= ?3",
				LogLevel.INFO, ".*" + last.id.toHexString() + ".*", now)));
		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and message like ?2 and timestamp >= ?3",
				LogLevel.INFO, ".+" + payload.source.channelName + ".+" + payload.target.channelName + ".+", now)));

	}

	/**
	 * Check that create a connection and not enable it.
	 */
	@Test
	public void shouldCreateConnectionAndNotEnableConnection() {

		final var payload = this.createValidCreateConnectionPayload();
		payload.enabled = false;

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createConnectionQueueName, payload));

		final TopologyConnectionEntity last = this
				.assertItemNotNull(TopologyConnectionEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= last.createTimestamp);
		assertTrue(last.createTimestamp <= last.updateTimestamp);
		assertNull(last.deletedTimestamp);
		assertEquals(payload.source, NodePayloadTest.from(last.source));
		assertEquals(payload.target, NodePayloadTest.from(last.target));
		assertFalse(last.enabled);

		assertFalse(this.listener.isOpen(payload.source.channelName));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and message like ?2 and timestamp >= ?3",
				LogLevel.INFO, ".*" + last.id.toHexString() + ".*", now)));

	}

	/**
	 * Create a valid create connection payload.
	 *
	 * @return a valid create connection payload.
	 */
	private CreateConnectionPayload createValidCreateConnectionPayload() {

		final var payload = new CreateConnectionPayload();
		payload.source = new NodePayload();
		payload.target = new NodePayload();
		PayloadSchema publish = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					NodePayload node = null;
					if (payload.source.componentId == null && channel.publish != null && channel.subscribe == null) {

						node = payload.source;

					} else if (payload.target.componentId == null && channel.publish == null
							&& channel.subscribe != null) {

						node = payload.target;
					}

					if (node != null) {

						node.componentId = component.id;
						node.channelName = channel.name;

						if (publish == null) {

							if (channel.publish != null) {

								publish = channel.publish;

							} else {

								publish = channel.subscribe;
							}

						} else {

							if (channel.publish != null) {

								channel.publish = publish;

							} else {

								channel.subscribe = publish;
							}
							this.assertItemNotNull(component.update());

						}
						break;

					}

				}
			}

		} while (payload.source.componentId == null || payload.target.componentId == null);

		return payload;
	}

	/**
	 * Check that create a connection where is subscribed a C2component and it is
	 * enabled.
	 */
	@Test
	public void shouldCreateConnectionAddC2SubscriptionsAndEnableConnection() {

		final var c0 = this.createComponent(ComponentType.C0);
		final var c1 = this.createComponent(ComponentType.C1);
		final var schema = PayloadSchemaTestCase.nextPayloadSchema(2);
		c0.channels.get(0).publish = schema;
		this.assertItemNotNull(c0.update());
		c1.channels.get(0).subscribe = schema;
		this.assertItemNotNull(c1.update());

		final List<ComponentEntity> c2s = new ArrayList<>();
		final var sentMessagePayloadSchema = createSentMessagePayloadSchemaFor(schema);
		for (var i = 0; i < 43; i++) {

			final var c2 = this.createComponent(ComponentType.C2);
			c2.channels.get(0).subscribe = sentMessagePayloadSchema;
			this.assertItemNotNull(c2.update());
			c2s.add(c2);
		}

		final var payload = new CreateConnectionPayload();
		payload.source = new NodePayload();
		payload.source.componentId = c0.id;
		payload.source.channelName = c0.channels.get(0).name;
		payload.target = new NodePayload();
		payload.target.componentId = c1.id;
		payload.target.channelName = c1.channels.get(0).name;
		payload.enabled = true;

		var now = TimeManager.now();
		final var expectedLogsCount = 1 + c2s.size();
		this.executeAndWaitUntilNewLogs(expectedLogsCount,
				() -> this.assertPublish(this.createConnectionQueueName, payload));

		final TopologyConnectionEntity last = this
				.assertItemNotNull(TopologyConnectionEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= last.createTimestamp);
		assertTrue(last.createTimestamp <= last.updateTimestamp);
		assertNull(last.deletedTimestamp);
		assertEquals(payload.source, NodePayloadTest.from(last.source));
		assertEquals(payload.target, NodePayloadTest.from(last.target));
		assertTrue(last.enabled);
		assertNotNull(last.notifications);
		C2: for (final var c2 : c2s) {

			for (final var notification : last.notifications) {

				if (notification.node.componentId.equals(c2.id)
						&& notification.node.channelName.equals(c2.channels.get(0).name)) {
					continue C2;
				}
			}

			fail("The c2s " + c2.id.toHexString() + " is not notified");
		}

		assertTrue(this.listener.isOpen(payload.source.channelName));
		assertEquals(2 + last.notifications.size(),
				this.assertItemNotNull(LogEntity.count("level = ?1 and message like ?2 and timestamp >= ?3",
						LogLevel.INFO, ".*" + last.id.toHexString() + ".*", now)));
		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and message like ?2 and timestamp >= ?3",
				LogLevel.INFO, ".+" + payload.source.channelName + ".+" + payload.target.channelName + ".+", now)));

		final var queues = new ArrayList<TestMQQueue>();
		for (final var c2 : c2s) {
			final var queue = this.waitOpenQueue(c2.channels.get(0).name);

			queues.add(queue);
		}
		final var msg = new JsonObject();
		now = TimeManager.now();
		this.executeAndWaitUntilNewLogs(1 + queues.size(), () -> this.assertPublish(c0.channels.get(0).name, msg));

		for (final var queue : queues) {

			final var received = queue.waitReceiveMessage(SentMessagePayload.class);
			assertEquals(last.id, received.connectionId);
			assertNotNull(received.source);
			assertEquals(c0.id, received.source.id);
			assertEquals(c0.name, received.source.name);
			assertEquals(c0.type, received.source.type);
			assertEquals(c1.id, received.target.id);
			assertEquals(c1.name, received.target.name);
			assertEquals(c1.type, received.target.type);
			assertTrue(now <= received.timestamp);
			assertEquals(msg, received.messagePayload);

		}
	}

	/**
	 * Create a C0 component to use in a test.
	 *
	 * @return the created C0 component.
	 */
	private ComponentEntity createComponent(ComponentType type) {

		final var component = new ComponentEntity();
		component.apiVersion = "1.0.0";
		component.description = type + " component";
		final var name = nextPattern("component_{0}");
		component.name = type.name().toLowerCase() + "_" + name;
		component.since = TimeManager.now();
		component.type = type;
		component.version = "1.0.0";
		component.channels = new ArrayList<ChannelSchema>();
		final var channel = new ChannelSchema();
		channel.name = nextPattern("valawai/" + type.name().toLowerCase() + "/" + name + "/control/action_{0}");
		channel.description = "channel of " + name;
		component.channels.add(channel);
		this.assertItemNotNull(component.persist());
		return component;
	}

	/**
	 * Check that create a connection where are subscribed some C2 components and it
	 * is not enabled.
	 */
	@Test
	public void shouldCreateConnectionAddMultipleC2SubscriptionsAndNotEnableConnection() {

		final var c0 = this.createComponent(ComponentType.C0);
		final var c1 = this.createComponent(ComponentType.C1);
		final var schema = PayloadSchemaTestCase.nextPayloadSchema(2);
		c0.channels.get(0).publish = schema;
		this.assertItemNotNull(c0.update());
		c1.channels.get(0).subscribe = schema;
		this.assertItemNotNull(c1.update());

		final List<ComponentEntity> c2s = new ArrayList<>();
		final var sentMessagePayloadSchema = createSentMessagePayloadSchemaFor(schema);
		for (var i = 0; i < 43; i++) {

			final var c2 = this.createComponent(ComponentType.C2);
			c2.channels.get(0).subscribe = sentMessagePayloadSchema;
			this.assertItemNotNull(c2.update());
			c2s.add(c2);
		}

		final var payload = new CreateConnectionPayload();
		payload.source = new NodePayload();
		payload.source.componentId = c0.id;
		payload.source.channelName = c0.channels.get(0).name;
		payload.target = new NodePayload();
		payload.target.componentId = c1.id;
		payload.target.channelName = c1.channels.get(0).name;
		payload.enabled = false;

		final var now = TimeManager.now();
		final var expectedLogsCount = 1 + c2s.size();
		this.executeAndWaitUntilNewLogs(expectedLogsCount,
				() -> this.assertPublish(this.createConnectionQueueName, payload));

		final TopologyConnectionEntity last = this
				.assertItemNotNull(TopologyConnectionEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= last.createTimestamp);
		assertTrue(last.createTimestamp <= last.updateTimestamp);
		assertNull(last.deletedTimestamp);
		assertEquals(payload.source, NodePayloadTest.from(last.source));
		assertEquals(payload.target, NodePayloadTest.from(last.target));
		assertFalse(last.enabled);
		assertNotNull(last.notifications);
		C2: for (final var c2 : c2s) {

			for (final var notification : last.notifications) {

				if (notification.node.componentId.equals(c2.id)
						&& notification.node.channelName.equals(c2.channels.get(0).name)) {

					continue C2;
				}
			}

			fail("The c2s " + c2.id.toHexString() + " is not notified");
		}

		assertFalse(this.listener.isOpen(payload.source.channelName));
		assertEquals(last.notifications.size() + 1,
				this.assertItemNotNull(LogEntity.count("level = ?1 and message like ?2 and timestamp >= ?3",
						LogLevel.INFO, ".*" + last.id.toHexString() + ".*", now)));

	}

	/**
	 * Check that can create a connection where the publish and subscribe do not
	 * match, because exist a convert code.
	 */
	@Test
	public void shouldCreateConnectionWithPublishNotMatchsubscribeBecauseExistConvertCode() {

		final var payload = new CreateConnectionPayloadTest().nextModel();
		payload.source.componentId = null;
		PayloadSchema publish = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null) {

						payload.source.componentId = component.id;
						payload.source.channelName = channel.name;
						publish = channel.publish;
						break;

					}
				}
			}

		} while (payload.source.componentId == null);
		payload.target.componentId = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null && !publish.match(channel.subscribe, new HashMap<>())) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);

		payload.enabled = false;

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createConnectionQueueName, payload));

		final TopologyConnectionEntity last = this
				.assertItemNotNull(TopologyConnectionEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= last.createTimestamp);
		assertTrue(last.createTimestamp <= last.updateTimestamp);
		assertNull(last.deletedTimestamp);
		assertEquals(payload.source, NodePayloadTest.from(last.source));
		assertEquals(payload.target, NodePayloadTest.from(last.target));
		assertFalse(last.enabled);

		assertFalse(this.listener.isOpen(payload.source.channelName));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and message like ?2 and timestamp >= ?3",
				LogLevel.INFO, ".*" + last.id.toHexString() + ".*", now)));
	}

}
