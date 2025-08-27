/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.live.components.ComponentEntities;
import eu.valawai.mov.persistence.live.logs.LogEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
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
	String createConnectionQueueName;

	/**
	 * Check that cannot create with an invalid payload.
	 */
	@Test
	public void shouldNotCreateConnectionWithInvalidPayload() {

		final var payload = new CreateNotificationPayload();
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.createConnectionQueueName, payload));

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and payload = ?2 and timestamp >= ?3",
				LogLevel.ERROR, Json.encodePrettily(payload), now)));
	}

	/**
	 * Check that cannot create with an undefined connection identifier.
	 */
	@Test
	public void shouldNotCreateConnectionWithUndefinedConnectionId() {

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = TopologyConnectionEntities.undefined();
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

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = TopologyConnectionEntities.nextTopologyConnection().id;
		payload.target.componentId = ComponentEntities.undefined();
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

		final var payload = new CreateNotificationPayloadTest().nextModel();
		payload.connectionId = TopologyConnectionEntities.nextTopologyConnection().id;
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

}
