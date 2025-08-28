/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.components.BasicPayloadSchemaTest;
import eu.valawai.mov.api.v1.components.PayloadSchema;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.live.components.ComponentEntities;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.logs.LogEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionNotification;
import eu.valawai.mov.persistence.live.topology.TopologyNode;
import eu.valawai.mov.services.LocalConfigService;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;
import jakarta.inject.Inject;

/**
 * Test the {@link CreateNotificationManager}.
 *
 * @see CreateNotificationManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class CreateNotificationManagerTest extends MovEventTestCase {

	/**
	 * The local configuration.
	 */
	@Inject
	LocalConfigService configuration;

	/**
	 * The queue name to send the create connection events.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.create_notification.queue.name", defaultValue = "valawai/topology/notification/create")
	String createNotificationQueueName;

	/**
	 * Clear the connections because some of them are not valid and can affect other
	 * tests.
	 */
	@AfterAll
	public static void clearConnections() {

		TopologyConnectionEntities.clear();

	}

	/**
	 * Check that cannot create with an invalid payload.
	 */
	@Test
	public void shouldNotCreateNotificationWithInvalidPayload() {

		final var payload = new CreateNotificationPayload();
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with an undefined connection identifier.
	 */
	@Test
	public void shouldNotCreateNotificationWithUndefinedConnectionId() {

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = TopologyConnectionEntities.undefined();
		payload.target.componentId = null;
		payload.converterJSCode = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with a deleted connection.
	 */
	@Test
	public void shouldNotCreateNotificationWithDeletedConnection() {

		final var payload = new CreateNotificationPayloadTest().nextModel();
		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		payload.connectionId = connection.id;
		connection.deletedTimestamp = ValueGenerator.nextPastTime();
		this.assertItemNotNull(connection.update());

		payload.target.componentId = null;
		payload.converterJSCode = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with an undefined target component.
	 */
	@Test
	public void shouldNotCreateNotificationWithUndefinedTargetComponentId() {

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = TopologyConnectionEntities.nextTopologyConnection().id;
		payload.target.componentId = ComponentEntities.undefined();
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with an undefined target channel.
	 */
	@Test
	public void shouldNotCreateNotificationWithUndefinedTargetChannel() {

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = TopologyConnectionEntities.nextTopologyConnection().id;
		payload.target.componentId = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						payload.target.componentId = component.id;
						payload.target.channelName += channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with a target without channels.
	 */
	@Test
	public void shouldNotCreateNotificationWithTargetWithoutChannels() {

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = TopologyConnectionEntities.nextTopologyConnection().id;
		final var component = ComponentEntities.nextComponent();
		component.channels = null;
		this.assertItemNotNull(component.update());
		payload.target.componentId = component.id;

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with a publish target channel.
	 */
	@Test
	public void shouldNotCreateNotificationWithPublishTargetChannel() {

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = TopologyConnectionEntities.nextTopologyConnection().id;
		payload.target.componentId = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with an undefined source component in the
	 * connection.
	 */
	@Test
	public void shouldNotCreateNotificationWithUndefinedSourceComponent() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.source.componentId = ComponentEntities.undefined();
		this.assertItemNotNull(connection.update());

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = connection.id;
		payload.target.componentId = null;
		payload.converterJSCode = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with a source without channels.
	 */
	@Test
	public void shouldNotCreateNotificationWithSourceWithoutChannels() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		final ComponentEntity source = this.assertItemNotNull(ComponentEntity.findById(connection.source.componentId));
		source.channels = null;
		this.assertItemNotNull(source.update());

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = connection.id;
		payload.target.componentId = null;
		payload.converterJSCode = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with an undefined source channel in the connection.
	 */
	@Test
	public void shouldNotCreateNotificationWithUndefinedSourceChannel() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.source.channelName += "/undefined/channel";
		this.assertItemNotNull(connection.update());

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = connection.id;
		payload.target.componentId = null;
		payload.converterJSCode = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with a source channel that is a subscription in the
	 * connection.
	 */
	@Test
	public void shouldNotCreateNotificationWithSubscribeSourceChannel() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		final ComponentEntity source = this.assertItemNotNull(ComponentEntity.findById(connection.source.componentId));
		for (final var channel : source.channels) {

			if (channel.name.equals(connection.source.channelName)) {

				channel.subscribe = channel.publish;
				channel.publish = null;
				break;
			}

		}
		this.assertItemNotNull(source.update());

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = connection.id;
		payload.target.componentId = null;
		payload.converterJSCode = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create when the source not match with the target.
	 */
	@Test
	public void shouldNotCreateNotificationWithUnmatchinfWithSource() {

		final var sourceSchema = new BasicPayloadSchemaTest().nextModel();
		var targetSchema = new BasicPayloadSchemaTest().nextModel();
		while (sourceSchema.match(targetSchema, new HashMap<>())) {

			targetSchema = new BasicPayloadSchemaTest().nextModel();
		}
		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		final ComponentEntity source = this.assertItemNotNull(ComponentEntity.findById(connection.source.componentId));
		for (final var channel : source.channels) {

			if (channel.name.equals(connection.source.channelName)) {

				channel.publish = sourceSchema;
				break;
			}

		}
		this.assertItemNotNull(source.update());

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = connection.id;
		payload.target.componentId = null;
		payload.converterJSCode = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						channel.subscribe = targetSchema;
						this.assertItemNotNull(component.update());
						break;

					}
				}
			}

		} while (payload.target.componentId == null);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check create notification when not match schemas because exist convert code.
	 */
	@Test
	public void shoulCreateNotificationBecauseExistConvertCode() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = connection.id;
		payload.target.componentId = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.DEBUG, Json.encodePrettily(payload), now)));

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertThat(updated.notifications, is(not(nullValue())));

		final var expected = new TopologyConnectionNotification();
		expected.enabled = payload.enabled;
		expected.node = new TopologyNode();
		expected.node.componentId = payload.target.componentId;
		expected.node.channelName = payload.target.channelName;
		expected.notificationMessageConverterJSCode = payload.converterJSCode;
		assertThat(updated.notifications, hasItem(expected));

	}

	/**
	 * Check create notification when match schemas.
	 */
	@Test
	public void shoulCreateNotificationBecauseMatchSchemas() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = connection.id;
		payload.target.componentId = null;
		payload.converterJSCode = null;
		PayloadSchema commonSchema = null;
		final ComponentEntity source = this.assertItemNotNull(ComponentEntity.findById(connection.source.componentId));
		for (final var channel : source.channels) {

			if (channel.name.equals(connection.source.channelName)) {

				commonSchema = channel.publish;
				break;
			}

		}
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						channel.subscribe = commonSchema;
						this.assertItemNotNull(component.update());
						break;

					}
				}
			}

		} while (payload.target.componentId == null);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.DEBUG, Json.encodePrettily(payload), now)));

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertThat(updated.notifications, is(not(nullValue())));

		final var expected = new TopologyConnectionNotification();
		expected.enabled = payload.enabled;
		expected.node = new TopologyNode();
		expected.node.componentId = payload.target.componentId;
		expected.node.channelName = payload.target.channelName;
		assertThat(updated.notifications, hasItem(expected));

	}

	/**
	 * Check create notification when match the previous subscription schema.
	 */
	@Test
	public void shoulCreateNotificationBecauseMatchPreviousSchema() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = connection.id;
		payload.target.componentId = null;
		payload.converterJSCode = null;
		PayloadSchema commonSchema = null;
		final ComponentEntity source = this.assertItemNotNull(ComponentEntity.findById(connection.source.componentId));
		for (final var channel : source.channels) {

			if (channel.name.equals(connection.source.channelName)) {

				commonSchema = channel.publish;
				break;
			}

		}
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						channel.subscribe = SentMessagePayload.createSentMessagePayloadSchemaFor(commonSchema);
						this.assertItemNotNull(component.update());
						break;

					}
				}
			}

		} while (payload.target.componentId == null);

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createNotificationQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.DEBUG, Json.encodePrettily(payload), now)));

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertThat(updated.notifications, is(not(nullValue())));

		final var expected = new TopologyConnectionNotification();
		expected.enabled = payload.enabled;
		expected.node = new TopologyNode();
		expected.node.componentId = payload.target.componentId;
		expected.node.channelName = payload.target.channelName;
		expected.notificationMessageConverterJSCode = payload.converterJSCode;
		assertThat(updated.notifications, hasItem(expected));

	}

}
