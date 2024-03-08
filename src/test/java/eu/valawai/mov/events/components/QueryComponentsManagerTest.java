/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import static eu.valawai.mov.ValueGenerator.nextUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.logs.LogEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;

/**
 * Test the {@link QueryComponentsManager}.
 *
 * @see QueryComponentsManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class QueryComponentsManagerTest extends MovEventTestCase {

	/**
	 * The name of the queue to send the query to obtain some components.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.query_components.queue.name", defaultValue = "valawai/component/query")
	String queryComponentstQueueName;

	/**
	 * Check that cannot ask for components with an invalid payload.
	 */
	@Test
	public void shouldNotQueryComponentsWithInvalidPayload() {

		final var payload = new QueryComponentsPayload();
		payload.offset = -1;

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.queryComponentstQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
	}

	/**
	 * Check that query some components.
	 */
	@Test
	public void shouldQuerySomeComponents() {

		final var payload = new QueryComponentsPayload();
		payload.offset = 3;
		payload.limit = 7;
		payload.pattern = nextUUID().toString();

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.queryComponentstQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.INFO, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));

	}

}
