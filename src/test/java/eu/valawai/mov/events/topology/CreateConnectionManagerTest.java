/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.components.ComponentEntities;
import eu.valawai.mov.persistence.logs.LogEntity;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;

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

		final var payload = new CreateConnectionPayload();
		payload.source = new NodePayload();
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null && channel.subscribe == null) {

						payload.source.componentId = component.id;
						payload.source.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.source.componentId == null);
		payload.target = new NodePayload();
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish == null && channel.subscribe != null) {

						payload.target.componentId = component.id;
						payload.target.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.target.componentId == null);
		payload.enabled = true;

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLogs(2, () -> this.assertPublish(this.createConnectionQueueName, payload));

		final TopologyConnectionEntity last = this
				.assertItemNotNull(TopologyConnectionEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= last.createTimestamp);
		assertEquals(last.updateTimestamp, last.createTimestamp);
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

		final var payload = new CreateConnectionPayload();
		payload.source = new NodePayload();
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null && channel.subscribe == null) {

						payload.source.componentId = component.id;
						payload.source.channelName = channel.name;
						break;

					}
				}
			}

		} while (payload.source.componentId == null);
		payload.target = new NodePayload();
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish == null && channel.subscribe != null) {

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
		assertEquals(last.updateTimestamp, last.createTimestamp);
		assertNull(last.deletedTimestamp);
		assertEquals(payload.source, NodePayloadTest.from(last.source));
		assertEquals(payload.target, NodePayloadTest.from(last.target));
		assertFalse(last.enabled);

		assertFalse(this.listener.isOpen(payload.source.channelName));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and message like ?2 and timestamp >= ?3",
				LogLevel.INFO, ".*" + last.id.toHexString() + ".*", now)));

	}

}
